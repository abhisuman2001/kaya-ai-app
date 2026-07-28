package com.example.data.model

enum class HazardCategory(val displayName: String, val iconName: String) {
    HELMET("Helmet Detection", "Hardhat"),
    VEST("Vest Detection", "Vest"),
    GLOVE("Glove Detection", "Hand"),
    FALL("Fall Protection", "Fall"),
    CRANE("Crane Safety Zone", "Crane"),
    ELECTRICAL("Electrical Panel", "Zap"),
    SCAFFOLD("Scaffolding Rig", "Construction"),
    FIRE("Fire & Thermal", "Flame")
}

data class HazardDetectionItem(
    val id: String,
    val category: HazardCategory,
    val title: String,
    val location: String,
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val timestamp: String,
    val isAcknowledged: Boolean = false,
    val audioAlertText: String,
    val oshaStandard: String,
    val description: String,
    val detectionConfidence: Int,
    val imageUrl: String = "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3"
)

data class HazardDetectionState(
    val hazards: List<HazardDetectionItem> = listOf(
        HazardDetectionItem(
            id = "hz_001",
            category = HazardCategory.HELMET,
            title = "Missing Safety Helmet on Deck",
            location = "Zone B-4 • Level 18 Slab",
            severity = "CRITICAL",
            timestamp = "14:24:02",
            isAcknowledged = false,
            audioAlertText = "Critical Hazard: Subcontractor worker at Level 18 Zone B-4 is operating without hardhat protection.",
            oshaStandard = "OSHA 1926.100(a)",
            detectionConfidence = 98,
            description = "Optical AI detected worker operating under active concrete bucket without ANSI Z89.1 headgear."
        ),
        HazardDetectionItem(
            id = "hz_002",
            category = HazardCategory.VEST,
            title = "Non-Reflective Vest Violation",
            location = "Grid C-2 • Rebar Staging",
            severity = "HIGH",
            timestamp = "14:22:15",
            isAcknowledged = false,
            audioAlertText = "High Risk: High-visibility vest required in active heavy equipment perimeter.",
            oshaStandard = "OSHA 1926.201(a)",
            detectionConfidence = 96,
            description = "Worker in dark clothing inside active Bobcat loader clearance radius."
        ),
        HazardDetectionItem(
            id = "hz_003",
            category = HazardCategory.GLOVE,
            title = "Missing Cut-Resistant Gloves",
            location = "Level 17 • Rebar Binding Station",
            severity = "MEDIUM",
            timestamp = "14:18:50",
            isAcknowledged = false,
            audioAlertText = "PPE Alert: Hand protection mandatory during rebar wire tying.",
            oshaStandard = "OSHA 1926.28(a)",
            detectionConfidence = 92,
            description = "Ironworker handling sharp tie-wire without Level A4 cut-resistant gloves."
        ),
        HazardDetectionItem(
            id = "hz_004",
            category = HazardCategory.FALL,
            title = "Unanchored Deck Perimeter Fall Risk",
            location = "Level 18 Deck Edge (East Line)",
            severity = "CRITICAL",
            timestamp = "14:15:30",
            isAcknowledged = false,
            audioAlertText = "Warning! Worker within 1.5 meters of open edge without dual-leg tie-off lanyard.",
            oshaStandard = "OSHA 1926.501(b)(1)",
            detectionConfidence = 99,
            description = "High elevation exposure above 10m without guardrail system or anchored harness."
        ),
        HazardDetectionItem(
            id = "hz_005",
            category = HazardCategory.CRANE,
            title = "Worker Under Suspended Crane Load",
            location = "Sector North • Tower Crane #2",
            severity = "CRITICAL",
            timestamp = "14:10:00",
            isAcknowledged = true,
            audioAlertText = "Danger: Crane swing radius breach with 2.5 ton rebar bundle overhead.",
            oshaStandard = "OSHA 1926.1425",
            detectionConfidence = 97,
            description = "Rigging zone breach. Optical tracking flagged 2 workers inside red blast zone."
        ),
        HazardDetectionItem(
            id = "hz_006",
            category = HazardCategory.ELECTRICAL,
            title = "Uncovered High-Voltage Sub-Panel",
            location = "Basement Level B2 • Utility Shaft",
            severity = "HIGH",
            timestamp = "13:55:12",
            isAcknowledged = true,
            audioAlertText = "Electrical Alert: Live 480V panel enclosure door missing lock.",
            oshaStandard = "OSHA 1926.405(b)(2)",
            detectionConfidence = 95,
            description = "480V temporary power panel left open with exposed live busbars in damp area."
        ),
        HazardDetectionItem(
            id = "hz_007",
            category = HazardCategory.SCAFFOLD,
            title = "Scaffolding Missing Guardrail & Tag",
            location = "East Facade Elevation • Stage 3",
            severity = "HIGH",
            timestamp = "13:42:08",
            isAcknowledged = true,
            audioAlertText = "Scaffolding Warning: Platform missing top rail and inspection tag.",
            oshaStandard = "OSHA 1926.451(g)(1)",
            detectionConfidence = 94,
            description = "Modular scaffolding platform lacks mid-rail and current green safety sign off tag."
        ),
        HazardDetectionItem(
            id = "hz_008",
            category = HazardCategory.FIRE,
            title = "Hot Work Spark Thermal Anomaly",
            location = "Core Wall Welding Grid A-1",
            severity = "MEDIUM",
            timestamp = "13:30:20",
            isAcknowledged = true,
            audioAlertText = "Fire Alert: Welding spark shower detected without fire blanket barrier.",
            oshaStandard = "OSHA 1926.352(d)",
            detectionConfidence = 91,
            description = "Thermal IR sensor detected 420°C hot work sparks within 3m of combustible lumber."
        )
    ),
    val activeVoicePlayingId: String? = null,
    val selectedCategoryFilter: HazardCategory? = null,
    val selectedSeverityFilter: String? = null // "CRITICAL", "HIGH", "MEDIUM", "LOW"
)
