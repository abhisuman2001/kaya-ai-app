package com.example.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AiIntegrationScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BlueprintScreen
import com.example.ui.screens.DeviceScreen
import com.example.ui.screens.HazardDetectionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KnowledgeScreen
import com.example.ui.screens.LiveAiScreen
import com.example.ui.screens.MaterialScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QualityScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SafetyScreen
import com.example.ui.screens.SceneAnalysisScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.OnboardingScreen
import com.example.ui.screens.auth.OtpScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.auth.RoleSelectionScreen
import com.example.ui.screens.auth.SplashScreen
import com.example.ui.theme.SiteMindTheme
import com.example.ui.viewmodel.SiteMindViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual-regression safety net (Phase 0). One baseline per screen per theme, rendered
 * against a real [SiteMindViewModel] so fixture/default state shows exactly as it does
 * on-device. Diff against these after every restyle phase; re-baseline intentional changes.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class ScreenBaselineTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun newViewModel(): SiteMindViewModel =
      SiteMindViewModel(ApplicationProvider.getApplicationContext())

  private fun capture(name: String, darkTheme: Boolean, content: @Composable () -> Unit) {
    composeTestRule.setContent { SiteMindTheme(darkTheme = darkTheme) { content() } }
    val suffix = if (darkTheme) "dark" else "light"
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/$name-$suffix.png")
  }

  // --- Auth flow (outside NavHost) ---

  @Test fun splashScreen_dark() = capture("SplashScreen", true) { SplashScreen(newViewModel(), {}) }
  @Test fun splashScreen_light() = capture("SplashScreen", false) { SplashScreen(newViewModel(), {}) }

  @Test fun onboardingScreen_dark() = capture("OnboardingScreen", true) { OnboardingScreen(newViewModel(), {}, {}) }
  @Test fun onboardingScreen_light() = capture("OnboardingScreen", false) { OnboardingScreen(newViewModel(), {}, {}) }

  @Test fun loginScreen_dark() = capture("LoginScreen", true) { LoginScreen(newViewModel(), {}, {}) }
  @Test fun loginScreen_light() = capture("LoginScreen", false) { LoginScreen(newViewModel(), {}, {}) }

  @Test fun registerScreen_dark() = capture("RegisterScreen", true) { RegisterScreen(newViewModel(), {}) }
  @Test fun registerScreen_light() = capture("RegisterScreen", false) { RegisterScreen(newViewModel(), {}) }

  @Test fun forgotPasswordScreen_dark() = capture("ForgotPasswordScreen", true) { ForgotPasswordScreen(newViewModel(), {}) }
  @Test fun forgotPasswordScreen_light() = capture("ForgotPasswordScreen", false) { ForgotPasswordScreen(newViewModel(), {}) }

  @Test fun otpScreen_dark() = capture("OtpScreen", true) { OtpScreen(newViewModel(), {}) }
  @Test fun otpScreen_light() = capture("OtpScreen", false) { OtpScreen(newViewModel(), {}) }

  @Test fun roleSelectionScreen_dark() = capture("RoleSelectionScreen", true) { RoleSelectionScreen(newViewModel(), {}) }
  @Test fun roleSelectionScreen_light() = capture("RoleSelectionScreen", false) { RoleSelectionScreen(newViewModel(), {}) }

  // --- Authenticated nav graph ---

  @Test fun homeScreen_dark() = capture("HomeScreen", true) { HomeScreen(newViewModel(), {}, {}, {}, {}, {}, {}) }
  @Test fun homeScreen_light() = capture("HomeScreen", false) { HomeScreen(newViewModel(), {}, {}, {}, {}, {}, {}) }

  @Test fun deviceScreen_dark() = capture("DeviceScreen", true) { DeviceScreen(newViewModel()) }
  @Test fun deviceScreen_light() = capture("DeviceScreen", false) { DeviceScreen(newViewModel()) }

  @Test fun liveAiScreen_dark() = capture("LiveAiScreen", true) { LiveAiScreen(newViewModel()) }
  @Test fun liveAiScreen_light() = capture("LiveAiScreen", false) { LiveAiScreen(newViewModel()) }

  @Test fun aiAssistantScreen_dark() = capture("AiAssistantScreen", true) { AiAssistantScreen(newViewModel()) }
  @Test fun aiAssistantScreen_light() = capture("AiAssistantScreen", false) { AiAssistantScreen(newViewModel()) }

  @Test fun hazardDetectionScreen_dark() = capture("HazardDetectionScreen", true) { HazardDetectionScreen(newViewModel()) }
  @Test fun hazardDetectionScreen_light() = capture("HazardDetectionScreen", false) { HazardDetectionScreen(newViewModel()) }

  @Test fun safetyScreen_dark() = capture("SafetyScreen", true) { SafetyScreen(newViewModel()) }
  @Test fun safetyScreen_light() = capture("SafetyScreen", false) { SafetyScreen(newViewModel()) }

  @Test fun tasksScreen_dark() = capture("TasksScreen", true) { TasksScreen(newViewModel(), {}) }
  @Test fun tasksScreen_light() = capture("TasksScreen", false) { TasksScreen(newViewModel(), {}) }

  @Test fun profileScreen_dark() = capture("ProfileScreen", true) { ProfileScreen(newViewModel()) }
  @Test fun profileScreen_light() = capture("ProfileScreen", false) { ProfileScreen(newViewModel()) }

  @Test fun reportScreen_dark() = capture("ReportScreen", true) { ReportScreen(newViewModel()) }
  @Test fun reportScreen_light() = capture("ReportScreen", false) { ReportScreen(newViewModel()) }

  @Test fun blueprintScreen_dark() = capture("BlueprintScreen", true) { BlueprintScreen(newViewModel()) }
  @Test fun blueprintScreen_light() = capture("BlueprintScreen", false) { BlueprintScreen(newViewModel()) }

  @Test fun sceneAnalysisScreen_dark() = capture("SceneAnalysisScreen", true) { SceneAnalysisScreen(newViewModel()) }
  @Test fun sceneAnalysisScreen_light() = capture("SceneAnalysisScreen", false) { SceneAnalysisScreen(newViewModel()) }

  @Test fun qualityScreen_dark() = capture("QualityScreen", true) { QualityScreen(newViewModel()) }
  @Test fun qualityScreen_light() = capture("QualityScreen", false) { QualityScreen(newViewModel()) }

  @Test fun materialScreen_dark() = capture("MaterialScreen", true) { MaterialScreen(newViewModel()) }
  @Test fun materialScreen_light() = capture("MaterialScreen", false) { MaterialScreen(newViewModel()) }

  @Test fun knowledgeScreen_dark() = capture("KnowledgeScreen", true) { KnowledgeScreen(newViewModel()) }
  @Test fun knowledgeScreen_light() = capture("KnowledgeScreen", false) { KnowledgeScreen(newViewModel()) }

  @Test fun analyticsScreen_dark() = capture("AnalyticsScreen", true) { AnalyticsScreen(newViewModel()) }
  @Test fun analyticsScreen_light() = capture("AnalyticsScreen", false) { AnalyticsScreen(newViewModel()) }

  @Test fun notificationsScreen_dark() = capture("NotificationsScreen", true) { NotificationsScreen(newViewModel(), {}) }
  @Test fun notificationsScreen_light() = capture("NotificationsScreen", false) { NotificationsScreen(newViewModel(), {}) }

  @Test fun aiIntegrationScreen_dark() = capture("AiIntegrationScreen", true) { AiIntegrationScreen(newViewModel()) }
  @Test fun aiIntegrationScreen_light() = capture("AiIntegrationScreen", false) { AiIntegrationScreen(newViewModel()) }
}
