package com.example.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

data class SupabaseSignUpUserMetadata(
    val display_name: String? = null,
    val site_role: String? = "worker",
    val email_verified: Boolean = true
)

data class SupabaseSignUpRequestDto(
    val email: String,
    val password: String,
    val email_confirm: Boolean = true,
    val user_metadata: SupabaseSignUpUserMetadata? = null,
    val data: SupabaseSignUpUserMetadata? = null
)

data class SupabaseSignInRequestDto(
    val email: String,
    val password: String
)

data class SupabaseAuthUser(
    val id: String,
    val email: String? = null,
    val user_metadata: SupabaseSignUpUserMetadata? = null
)

data class SupabaseAuthResponseDto(
    val access_token: String? = null,
    val token_type: String? = null,
    val user: SupabaseAuthUser? = null,
    val error: String? = null,
    val error_description: String? = null,
    val msg: String? = null
)

data class SupabaseSiteEventDto(
    val id: String? = null,
    val type: String = "HAZARD",
    val title: String,
    val description: String? = null,
    val project_id: String = "project_001",
    val level_id: String? = null,
    val zone_id: String? = null,
    val created_by_label: String? = "Supervisor",
    val created_by_role: String? = "SUPERVISOR",
    val assigned_to: String? = null,
    val severity: String = "HIGH",
    val status: String = "OPEN",
    val created_at: String? = null
)

data class SupabaseReportDto(
    val id: String? = null,
    val project_id: String = "project_001",
    val title: String,
    val summary: String? = null,
    val body: String? = null,
    val created_at: String? = null
)

data class SupabaseTaskDto(
    val id: String? = null,
    val project_id: String = "project_001",
    val title: String,
    val description: String? = null,
    val status: String = "todo",
    val priority: String = "medium",
    val assigned_to: String? = null,
    val created_by: String? = null,
    val created_at: String? = null
)

data class SupabaseBlueprintDto(
    val id: String? = null,
    val project_id: String = "project_001",
    val name: String,
    val code: String? = null,
    val revision: String? = null,
    val discipline: String? = null,
    val status: String = "APPROVED",
    val created_at: String? = null
)

data class SupabaseDeviceDto(
    val id: String? = null,
    val user_id: String? = null,
    val name: String,
    val firmware: String? = null,
    val battery_level: Int = 100,
    val connection_state: String = "connected",
    val project_id: String = "project_001",
    val last_seen_at: String? = null
)

// Upserted by the app (see worker_status table comment); read by the dashboard
// roster and monitoring page. ai_session must be exactly "active" | "idle" | "offline"
// — a Postgres enum on the worker_status.ai_session column.
data class SupabaseWorkerStatusDto(
    val user_id: String,
    val device_id: String? = null,
    val project_id: String? = "project_001",
    val level_id: String? = null,
    val zone_id: String? = null,
    val task: String? = null,
    val ai_session: String = "offline",
    val hazard: String? = null,
    val hazard_severity: String? = null,
    val last_active_at: String? = null
)

data class SupabaseProfileDto(
    val id: String,
    val display_name: String? = null,
    val email: String? = null,
    val site_role: String? = null,
    val approval_status: String? = "approved"
)

/**
 * A capture from the `media_assets` table — the same rows the dashboard counts for its
 * "Captures analysed" metric. `storage_path` points into a private Storage bucket, so it
 * needs a signed URL before it can be displayed.
 */
data class SupabaseMediaAssetDto(
    val id: String? = null,
    val user_id: String? = null,
    val project_id: String? = "project_001",
    val level_id: String? = null,
    val zone_id: String? = null,
    val session_id: String? = null,
    val type: String = "photo",
    val source: String = "glasses",
    val title: String? = null,
    val storage_path: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration_ms: Int? = null,
    val byte_size: Int? = null,
    val captured_at: String? = null,
    val sync_status: String = "pending",
    val ai_status: String = "pending",
    val ai_provider: String? = null,
    val ai_model: String? = null,
    val analyzed_at: String? = null
)

/** A row from `projects` — needed for the project picker and to stop hardcoding project ids. */
data class SupabaseProjectDto(
    val id: String,
    val name: String,
    val code: String? = null,
    val client: String? = null,
    val location: String? = null,
    val description: String? = null,
    val status: String? = "active",
    val phase: String? = null,
    val progress: Int? = 0,
    val budget: String? = null
)

interface SiteMindApiService {

    @POST("../auth/v1/admin/users")
    suspend fun adminSignUp(
        @Body request: SupabaseSignUpRequestDto
    ): Response<SupabaseAuthUser>

    @POST("../auth/v1/signup")
    suspend fun signUp(
        @Body request: SupabaseSignUpRequestDto
    ): Response<SupabaseAuthResponseDto>

    @POST("../auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body request: SupabaseSignInRequestDto
    ): Response<SupabaseAuthResponseDto>

    @POST("profiles")
    suspend fun createProfile(
        @Body profile: SupabaseProfileDto
    ): Response<List<SupabaseProfileDto>>

    @GET("site_events")
    suspend fun getHazards(
        @Query("select") select: String = "*",
        @Query("type") type: String = "eq.HAZARD"
    ): Response<List<SupabaseSiteEventDto>>

    @POST("site_events")
    suspend fun postHazard(@Body request: SupabaseSiteEventDto): Response<List<SupabaseSiteEventDto>>

    @PATCH("site_events")
    suspend fun updateHazardStatus(
        @Query("id") idQuery: String,
        @Body statusBody: Map<String, String>
    ): Response<List<SupabaseSiteEventDto>>

    @GET("reports")
    suspend fun getReports(@Query("select") select: String = "*"): Response<List<SupabaseReportDto>>

    @POST("reports")
    suspend fun postReport(@Body request: SupabaseReportDto): Response<List<SupabaseReportDto>>

    @GET("tasks")
    suspend fun getTasks(@Query("select") select: String = "*"): Response<List<SupabaseTaskDto>>

    @POST("tasks")
    suspend fun postTask(@Body request: SupabaseTaskDto): Response<List<SupabaseTaskDto>>

    @PATCH("tasks")
    suspend fun updateTaskStatus(
        @Query("id") idQuery: String,
        @Body statusBody: Map<String, String>
    ): Response<List<SupabaseTaskDto>>

    @GET("blueprints")
    suspend fun getBlueprints(@Query("select") select: String = "*"): Response<List<SupabaseBlueprintDto>>

    @POST("blueprints")
    suspend fun postBlueprint(@Body request: SupabaseBlueprintDto): Response<List<SupabaseBlueprintDto>>

    @GET("devices")
    suspend fun getDevices(@Query("select") select: String = "*"): Response<List<SupabaseDeviceDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("devices")
    suspend fun upsertDevice(
        @Body request: SupabaseDeviceDto,
        @Query("on_conflict") onConflict: String = "id"
    ): Response<List<SupabaseDeviceDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("worker_status")
    suspend fun upsertWorkerStatus(
        @Body request: SupabaseWorkerStatusDto,
        @Query("on_conflict") onConflict: String = "user_id"
    ): Response<List<SupabaseWorkerStatusDto>>

    @GET("profiles")
    suspend fun getProfiles(@Query("select") select: String = "*"): Response<List<SupabaseProfileDto>>

    // ---- Media ----

    /** Newest captures first, matching how the dashboard and v1's gallery order them. */
    @GET("media_assets")
    suspend fun getMediaAssets(
        @Query("select") select: String = "*",
        @Query("order") order: String = "captured_at.desc",
        @Query("limit") limit: Int = 100
    ): Response<List<SupabaseMediaAssetDto>>

    @POST("media_assets")
    suspend fun postMediaAsset(
        @Body request: SupabaseMediaAssetDto
    ): Response<List<SupabaseMediaAssetDto>>

    // ---- Projects ----

    @GET("projects")
    suspend fun getProjects(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.asc"
    ): Response<List<SupabaseProjectDto>>
}


