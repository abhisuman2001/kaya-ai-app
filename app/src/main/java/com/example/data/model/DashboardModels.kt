package com.example.data.model

data class WeatherInfo(
    val tempFahrenheit: Int = 78,
    val condition: String = "Clear & Dry",
    val windSpeedMph: Int = 8,
    val windDirection: String = "NW",
    val humidityPercent: Int = 42,
    val uvIndex: String = "Low (2)",
    val airQualityIndex: Int = 32,
    val safetyStatus: String = "Optimal for Crane Lifting & High-Rise Glazing"
)

data class ProjectInfo(
    val name: String = "Metro Tower B-4 High-Rise Construction",
    val location: String = "450 Metro Blvd, Sector 7",
    val currentStage: String = "Level 18 Deck Pour & Steel Framing",
    val progressPercent: Float = 0.72f,
    val targetCompletion: String = "Oct 2026",
    val activeContractorsCount: Int = 8
)

data class ShiftInfo(
    val shiftName: String = "Day / Evening Inspection Shift",
    val shiftHours: String = "08:00 - 18:00",
    val activeWorkerCount: Int = 22,
    val shiftLeadName: String = "Marcus Vance (Superintendent)",
    val briefingCompleted: Boolean = true,
    val emergencyRadioChannel: String = "Ch 4 (312.45 MHz)"
)

data class SiteTaskItem(
    val id: String,
    val title: String,
    val location: String,
    val category: String,
    val priority: String, // HIGH, MEDIUM, LOW
    val isCompleted: Boolean = false,
    val dueTime: String = "17:30"
)
