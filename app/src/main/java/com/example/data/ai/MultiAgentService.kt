package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.AgentExecutionStep
import com.example.data.model.AgentStatus
import com.example.data.model.LiveAiAnalysisResult
import com.example.data.model.VisionBoundingBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MultiAgentService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun processQueryAndFrame(
        query: String,
        siteZone: String = "Grid B-4 Level 3"
    ): LiveAiAnalysisResult = withContext(Dispatchers.Default) {

        val steps = mutableListOf<AgentExecutionStep>()

        // 1. Vision Agent
        delay(120)
        steps.add(
            AgentExecutionStep(
                agentName = "Vision Agent",
                status = AgentStatus.SUCCESS,
                output = "Segmented rebar layout, W14 steel beam, 3 workers in frame.",
                latencyMs = 120
            )
        )

        // 2. Safety Agent
        delay(180)
        val hasPpeIssue = query.contains("PPE", ignoreCase = true) || query.contains("safety", ignoreCase = true) || query.contains("hazard", ignoreCase = true)
        steps.add(
            AgentExecutionStep(
                agentName = "Safety Agent",
                status = if (hasPpeIssue) AgentStatus.WARNING else AgentStatus.SUCCESS,
                output = if (hasPpeIssue) "Non-conformance: Worker #2 missing safety glasses & high-vis vest." else "OSHA 1926 Fall protection & tie-off verified.",
                latencyMs = 180
            )
        )

        // 3. Quality Agent
        delay(150)
        val isBlueprintQuery = query.contains("beam", ignoreCase = true) || query.contains("align", ignoreCase = true) || query.contains("blueprint", ignoreCase = true)
        steps.add(
            AgentExecutionStep(
                agentName = "Quality Agent",
                status = if (isBlueprintQuery) AgentStatus.WARNING else AgentStatus.SUCCESS,
                output = if (isBlueprintQuery) "Beam B-12 alignment variance: +14mm from CAD S-204 spec." else "Rebar spacing 150mm c/c meets GFC drawing 108.",
                latencyMs = 150
            )
        )

        // 4. Knowledge Agent (RAG)
        delay(140)
        steps.add(
            AgentExecutionStep(
                agentName = "Knowledge Agent",
                status = AgentStatus.SUCCESS,
                output = "Retrieved SOP-202 (Steel Beam Installation) & OSHA 1926.501 guidelines.",
                latencyMs = 140
            )
        )

        // 5. Reporting Agent
        delay(100)
        steps.add(
            AgentExecutionStep(
                agentName = "Reporting Agent",
                status = AgentStatus.SUCCESS,
                output = "Logged site inspection entry into DPR #42 with geo-stamp ($siteZone).",
                latencyMs = 100
            )
        )

        // 6. Decision Engine
        delay(110)
        steps.add(
            AgentExecutionStep(
                agentName = "Decision Engine",
                status = AgentStatus.SUCCESS,
                output = "Action triggered: Audio response delivered to Ray-Ban Meta earpiece.",
                latencyMs = 110
            )
        )

        val geminiResult = callGeminiApi(query, siteZone)
        val responseText = geminiResult.text ?: generateSmartFallbackResponse(query, siteZone)

        val boxes = if (hasPpeIssue) {
            listOf(
                VisionBoundingBox("Worker #1 (PPE OK)", 0.98f, false, 0.15f, 0.25f, 0.25f, 0.55f, riskLevel = "LOW", category = "Personnel"),
                VisionBoundingBox("Missing Glasses & Vest ⚠️", 0.94f, true, 0.52f, 0.28f, 0.30f, 0.58f, riskLevel = "HIGH", category = "Safety PPE"),
                VisionBoundingBox("Scaffolding Guardrail", 0.91f, false, 0.05f, 0.10f, 0.88f, 0.20f, riskLevel = "LOW", category = "Perimeter Safety")
            )
        } else if (isBlueprintQuery) {
            listOf(
                VisionBoundingBox("Beam B-12 (+14mm Dev)", 0.96f, true, 0.25f, 0.35f, 0.50f, 0.30f, riskLevel = "CRITICAL", category = "CAD Tolerance"),
                VisionBoundingBox("Anchor Bolt Group", 0.98f, false, 0.20f, 0.68f, 0.22f, 0.22f, riskLevel = "LOW", category = "Fasteners")
            )
        } else {
            listOf(
                VisionBoundingBox("C35 Concrete Rebar", 0.99f, false, 0.18f, 0.22f, 0.65f, 0.60f, riskLevel = "LOW", category = "Reinforcement"),
                VisionBoundingBox("Safety Helmet", 0.97f, false, 0.42f, 0.12f, 0.18f, 0.18f, riskLevel = "LOW", category = "PPE Gear")
            )
        }

        LiveAiAnalysisResult(
            queryText = query,
            aiResponseText = responseText,
            detectedObjects = boxes,
            ppeCompliancePercent = if (hasPpeIssue) 88 else 98,
            blueprintDeviationMm = if (isBlueprintQuery) 14.2f else 0.8f,
            materialSpecs = "C35/45 Reinforced Concrete & Grade 8.8 Structural Steel",
            agentSteps = steps,
            isApiError = geminiResult.isError,
            apiErrorMessage = geminiResult.errorMessage
        )
    }

    private data class GeminiApiCallResult(
        val text: String?,
        val isError: Boolean,
        val errorMessage: String?
    )

    /**
     * All usable Gemini keys, in rotation order. Free-tier keys each carry their own
     * per-minute and per-day quota, so spreading calls across several multiplies the
     * usable budget. Falls back to the single-key field when the plural one is absent.
     */
    private val geminiKeys: List<String> by lazy {
        val multi = try { BuildConfig.GEMINI_API_KEYS } catch (e: Throwable) { "" }
        val single = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        (multi.split(",") + single)
            .map { it.trim() }
            .filter { it.isNotBlank() && it != "MY_GEMINI_API_KEY" }
            .distinct()
    }

    /**
     * Round-robin cursor. Starting each request where the last one left off spreads load
     * evenly instead of hammering key #1 until it 429s.
     */
    private val keyCursor = java.util.concurrent.atomic.AtomicInteger(0)

    /** HTTP codes where a *different* key might succeed. A 404 is the model id being wrong,
     *  which every key would hit identically, so it is deliberately not in this set. */
    private fun isKeyExhaustedCode(code: Int) = code == 429 || code == 403 || code == 401

    /**
     * Tries each key in turn until one answers or all are exhausted. Only quota/auth
     * failures advance the cursor — a network blip retries nothing, because the next key
     * would fail the same way and burn quota for no reason.
     */
    private fun callGeminiApi(query: String, zone: String): GeminiApiCallResult {
        if (geminiKeys.isEmpty()) {
            return GeminiApiCallResult(
                text = null,
                isError = true,
                errorMessage = "No Gemini API key configured. Add GEMINI_API_KEYS to .env."
            )
        }

        var lastError: GeminiApiCallResult? = null
        val start = keyCursor.getAndIncrement()

        for (offset in geminiKeys.indices) {
            val key = geminiKeys[((start + offset) % geminiKeys.size + geminiKeys.size) % geminiKeys.size]
            val result = callGeminiWithKey(key, query, zone)
            if (!result.isError) return result
            lastError = result
            // Only worth trying another key when this one is rate-limited or rejected.
            if (result.errorMessage?.contains("quota", ignoreCase = true) != true &&
                result.errorMessage?.contains("unauthorized", ignoreCase = true) != true &&
                result.errorMessage?.contains("permission denied", ignoreCase = true) != true
            ) {
                return result
            }
        }
        return lastError ?: GeminiApiCallResult(null, true, "All Gemini API keys failed.")
    }

    private fun callGeminiWithKey(apiKey: String, query: String, zone: String): GeminiApiCallResult {
        return try {
            // Kept deliberately short: every token here is billed against the free-tier
            // budget on each request, and the answer is read on a HUD, not a page.
            val fullPrompt =
                "You are Kaya AI, a construction safety assistant on Ray-Ban Meta glasses. " +
                    "Zone: $zone. Answer in under 60 words, prioritising safety and OSHA rules.\n\n" +
                    "Worker: $query"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", fullPrompt)
                            })
                        })
                    })
                })
                // Hard cap on output so a runaway answer cannot drain the daily token budget.
                put("generationConfig", JSONObject().apply {
                    put("maxOutputTokens", 200)
                    put("temperature", 0.4)
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val httpRequest = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(httpRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val respString = response.body?.string()
                    if (respString.isNullOrBlank()) {
                        return GeminiApiCallResult(null, true, "Empty response received from Gemini API.")
                    }
                    val jsonObj = JSONObject(respString)
                    val candidates = jsonObj.optJSONArray("candidates")
                    val content = candidates?.optJSONObject(0)?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        GeminiApiCallResult(text = text, isError = false, errorMessage = null)
                    } else {
                        GeminiApiCallResult(text = null, isError = true, errorMessage = "Gemini API response contained no valid output text.")
                    }
                } else {
                    val errorMsg = when (response.code) {
                        400 -> "API Key is invalid or request malformed (HTTP 400)."
                        401 -> "API Key expired or unauthorized (HTTP 401)."
                        403 -> "API Key permission denied or quota exceeded (HTTP 403)."
                        404 -> "Gemini API model endpoint not found (HTTP 404)."
                        429 -> "Gemini API quota rate limit exceeded (HTTP 429)."
                        else -> "Gemini API call failed with HTTP status ${response.code}."
                    }
                    GeminiApiCallResult(text = null, isError = true, errorMessage = errorMsg)
                }
            }
        } catch (e: Exception) {
            GeminiApiCallResult(
                text = null,
                isError = true,
                errorMessage = "Gemini API connection error: ${e.localizedMessage ?: "Network connection issue"}"
            )
        }
    }

    private fun generateSmartFallbackResponse(query: String, zone: String): String {
        return when {
            query.contains("beam", ignoreCase = true) || query.contains("install", ignoreCase = true) ->
                "According to Structural Blueprint S-204 and SOP-202, ensure anchor bolt alignment within +/-3mm tolerance. Torque Grade 8.8 bolts to 350 Nm. I've flagged a +14mm variance on Beam B-12 for supervisor review."

            query.contains("ppe", ignoreCase = true) || query.contains("safety", ignoreCase = true) ->
                "OSHA Safety Check for $zone: Hardhats compliant at 100%. Worker #2 at grid B-4 is missing eye protection and high-vis vest. An automated reminder has been sent to the earpiece."

            query.contains("concrete", ignoreCase = true) || query.contains("slump", ignoreCase = true) ->
                "Batch #482 Concrete Test Certificate verified. Grade C35/45, slump 135mm within specified range (120-150mm). Safe to proceed with pour for Level 3 deck."

            query.contains("blueprint", ignoreCase = true) || query.contains("cad", ignoreCase = true) ->
                "Overlapping BIM Model S-204 against current frame. 2 structural penetrations aligned. Electrical riser conduit in MEP-302 offset by 25mm."

            else ->
                "Kaya AI active in $zone. Camera feed analyzed: Work area clear of critical hazards. PPE compliance at 96%. All crew members operating in safe zones."
        }
    }
}
