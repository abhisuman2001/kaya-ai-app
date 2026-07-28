package com.example.data.model

enum class UserRole(
    val title: String,
    val description: String,
    val iconName: String
) {
    SAFETY_INSPECTOR("Safety Inspector", "Enforce OSHA compliance & log active site hazards", "Shield"),
    SITE_ENGINEER("Site Engineer", "Verify BIM specs & spatial CAD alignment via AR", "Architecture"),
    PROJECT_MANAGER("Project Manager", "Review daily shift logs, crew stats & auto reports", "Assignment"),
    FIELD_WORKER("Field Worker", "Hands-free voice queries & instant SOP lookup", "Psychology")
}

enum class AuthScreenState {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    OTP_VERIFICATION,
    ROLE_SELECTION,
    AUTHENTICATED
}

data class UserProfile(
    val id: String = "user_101",
    val name: String = "Marcus Vance",
    val email: String = "marcus.vance@sitemind.ai",
    val role: UserRole = UserRole.SAFETY_INSPECTOR,
    val company: String = "BuildTech Global Engineering",
    val jwtToken: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
    val isGoogleAuth: Boolean = false,
    val avatarUrl: String = "",
    val siteLocation: String = "Metro Tower Construction — Level 18",
    val connectedGlassesModel: String = "Ray-Ban Meta Smart Glasses (Gen 2)",
    val glassesBattery: Int = 88,
    val glassesStatus: String = "Connected & Active",
    val language: String = "English (US)",
    val theme: String = "Dark Mode",
    val isBiometricEnabled: Boolean = true,
    val isTelemetryShared: Boolean = true,
    val isLocationTrackingEnabled: Boolean = true,
    val isLoggedOut: Boolean = false
)
