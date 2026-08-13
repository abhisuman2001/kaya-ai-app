package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.AuthScreenState
import androidx.compose.material.icons.filled.CenterFocusStrong
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.BlueprintScreen
import com.example.ui.screens.DeviceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KnowledgeScreen
import com.example.ui.screens.LiveAiScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SafetyScreen
import com.example.ui.screens.SceneAnalysisScreen
import com.example.ui.screens.HazardDetectionScreen
import com.example.ui.screens.QualityScreen
import com.example.ui.screens.MaterialScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.AiIntegrationScreen
import com.example.ui.screens.MediaScreen
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.RecordVoiceOver
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.OnboardingScreen
import com.example.ui.screens.auth.OtpScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.auth.RoleSelectionScreen
import com.example.ui.screens.auth.SplashScreen
import com.example.ui.components.KayaBottomNav
import com.example.ui.components.KayaNavItem
import com.example.ui.viewmodel.SiteMindViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Dashboard", Icons.Default.Home)
    object LiveAi : Screen("live_ai", "Live AI", Icons.Default.Psychology)
    object Assistant : Screen("assistant", "Voice AI", Icons.Default.RecordVoiceOver)
    object Device : Screen("device", "Glasses", Icons.Default.Smartphone)
    object Tasks : Screen("tasks", "Tasks", Icons.Default.Assignment)
    object SceneAnalysis : Screen("scene_analysis", "Scene AI", Icons.Default.CenterFocusStrong)
    object HazardDetection : Screen("hazard_detection", "Hazards", Icons.Default.Warning)
    object Safety : Screen("safety", "Safety", Icons.Default.Shield)
    object Blueprints : Screen("blueprints", "CAD / BIM", Icons.Default.Architecture)
    object Quality : Screen("quality", "Quality", Icons.Default.FactCheck)
    object Material : Screen("material", "Material", Icons.Default.Inventory2)
    object Knowledge : Screen("knowledge", "SOP RAG", Icons.Default.MenuBook)
    object Reports : Screen("reports", "Reports", Icons.Default.Assignment)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.BarChart)
    object Notifications : Screen("notifications", "Alerts", Icons.Default.NotificationsActive)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object AiIntegration : Screen("ai_integration", "AI Engine", Icons.Default.AutoAwesome)
    object Media : Screen("media", "Media", Icons.Default.PhotoLibrary)
}

@Composable
fun AppNavigation(
    viewModel: SiteMindViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        AuthScreenState.SPLASH -> {
            SplashScreen(
                viewModel = viewModel,
                onContinue = { viewModel.navigateToAuth(AuthScreenState.ONBOARDING) }
            )
        }
        AuthScreenState.ONBOARDING -> {
            OnboardingScreen(
                viewModel = viewModel,
                onSkip = { viewModel.navigateToAuth(AuthScreenState.LOGIN) },
                onFinish = { viewModel.navigateToAuth(AuthScreenState.LOGIN) }
            )
        }
        AuthScreenState.LOGIN -> {
            LoginScreen(
                viewModel = viewModel,
                onNavigateRegister = { viewModel.navigateToAuth(AuthScreenState.REGISTER) },
                onNavigateForgotPassword = { viewModel.navigateToAuth(AuthScreenState.FORGOT_PASSWORD) }
            )
        }
        AuthScreenState.REGISTER -> {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateLogin = { viewModel.navigateToAuth(AuthScreenState.LOGIN) }
            )
        }
        AuthScreenState.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBack = { viewModel.navigateToAuth(AuthScreenState.LOGIN) }
            )
        }
        AuthScreenState.OTP_VERIFICATION -> {
            OtpScreen(
                viewModel = viewModel,
                onNavigateBack = { viewModel.navigateToAuth(AuthScreenState.REGISTER) }
            )
        }
        AuthScreenState.ROLE_SELECTION -> {
            RoleSelectionScreen(
                viewModel = viewModel,
                onRoleConfirmed = { viewModel.navigateToAuth(AuthScreenState.AUTHENTICATED) }
            )
        }
        AuthScreenState.AUTHENTICATED -> {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

            val bottomNavItems = listOf(
                Screen.Home,
                Screen.LiveAi,
                Screen.Assistant,
                Screen.Tasks,
                Screen.Profile
            )

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    // v1's floating pill nav. Kept inside bottomBar so Scaffold still
                    // reserves its height and content is never hidden behind it.
                    KayaBottomNav(
                        items = bottomNavItems.map {
                            KayaNavItem(route = it.route, label = it.title, icon = it.icon)
                        },
                        currentRoute = currentRoute,
                        onSelect = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 840.dp)
                    ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToLiveAi = { navController.navigate(Screen.LiveAi.route) },
                            onNavigateToSafety = { navController.navigate(Screen.Safety.route) },
                            onNavigateToBlueprints = { navController.navigate(Screen.Blueprints.route) },
                            onNavigateToKnowledge = { navController.navigate(Screen.Knowledge.route) },
                            onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                            // v1's hero card links to the media gallery ("View Media"), not the
                            // device page. The parameter name is fixed by the screen signature,
                            // which is part of the app's navigation contract.
                            onNavigateToDevice = { navController.navigate(Screen.Media.route) }
                        )
                    }
                    composable(Screen.Tasks.route) {
                        TasksScreen(
                            viewModel = viewModel,
                            onNavigateToVoiceAi = { prompt ->
                                navController.navigate(Screen.Assistant.route)
                            }
                        )
                    }
                    composable(Screen.Analytics.route) {
                        AnalyticsScreen(viewModel = viewModel)
                    }
                    composable(Screen.Notifications.route) {
                        NotificationsScreen(
                            viewModel = viewModel,
                            onNavigateToRoute = { route -> navController.navigate(route) }
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(viewModel = viewModel)
                    }
                    composable(Screen.AiIntegration.route) {
                        AiIntegrationScreen(viewModel = viewModel)
                    }
                    composable(Screen.Media.route) {
                        MediaScreen(viewModel = viewModel)
                    }
                    composable(Screen.Assistant.route) {
                        AiAssistantScreen(viewModel = viewModel)
                    }
                    composable(Screen.LiveAi.route) {
                        LiveAiScreen(viewModel = viewModel)
                    }
                    composable(Screen.SceneAnalysis.route) {
                        SceneAnalysisScreen(viewModel = viewModel)
                    }
                    composable(Screen.HazardDetection.route) {
                        HazardDetectionScreen(viewModel = viewModel)
                    }
                    composable(Screen.Safety.route) {
                        SafetyScreen(viewModel = viewModel)
                    }
                    composable(Screen.Device.route) {
                        DeviceScreen(
                            viewModel = viewModel,
                            onNavigateToRoute = { route -> navController.navigate(route) }
                        )
                    }
                    composable(Screen.Blueprints.route) {
                        BlueprintScreen(viewModel = viewModel)
                    }
                    composable(Screen.Quality.route) {
                        QualityScreen(viewModel = viewModel)
                    }
                    composable(Screen.Material.route) {
                        MaterialScreen(viewModel = viewModel)
                    }
                    composable(Screen.Knowledge.route) {
                        KnowledgeScreen(viewModel = viewModel)
                    }
                    composable(Screen.Reports.route) {
                        ReportScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
}
