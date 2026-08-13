package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AuthScreenState
import com.example.data.model.UserRole
import com.example.ui.components.ErrorBanner
import com.example.ui.components.KayaBrandHero
import com.example.ui.components.KayaHeroArt
import com.example.ui.components.KayaHeroSize
import com.example.ui.components.KayaGhostButton
import com.example.ui.components.KayaPrimaryButton
import com.example.ui.components.KayaSecondaryButton
import com.example.ui.components.TintIcon
import com.example.ui.components.TintPill
import com.example.ui.components.kayaOutlinedTextFieldColors
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeLarge
import com.example.ui.theme.ShapeMedium
import com.example.ui.theme.ShapeXLarge
import com.example.ui.theme.ShapeXXLarge
import com.example.ui.viewmodel.SiteMindViewModel
import kotlinx.coroutines.delay

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(
    viewModel: SiteMindViewModel,
    onContinue: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2200)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Kaya AI leads with the site, not the eyewear.
            KayaBrandHero(art = KayaHeroArt.Helmet, size = KayaHeroSize.Large)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kaya AI",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "See more. Build safer.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(44.dp))

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = "Construction intelligence, on site",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

// ==========================================
// 2. ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(
    viewModel: SiteMindViewModel,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }

    // Plain-language copy, in v1's voice: say what the worker gets, not the technology stack.
    val pages = listOf(
        OnboardingPageData(
            title = "AI that sees what you see",
            description = "Look at the site and Kaya AI describes it back — spotting missing PPE and hazards before they become incidents.",
            icon = Icons.Default.Psychology,
            badge = "Site safety"
        ),
        OnboardingPageData(
            title = "Plans, checked on site",
            description = "Hold the drawings up against what's actually built. Kaya AI flags where the two don't match.",
            icon = Icons.Default.Architecture,
            badge = "Quality"
        ),
        OnboardingPageData(
            title = "Reports that write themselves",
            description = "Just say what happened. Crew counts, hazards and daily logs are filed for you and reach your supervisor.",
            icon = Icons.Default.Assignment,
            badge = "Less paperwork"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
            .testTag("onboarding_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(LocalKayaColors.current.status.success, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kaya AI Active",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LocalKayaColors.current.status.success
                )
            }

            TextButton(
                onClick = onSkip,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        val page = pages[currentPage]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, ShapeXXLarge),
                shape = ShapeXXLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TintIcon(
                        icon = page.icon,
                        tint = LocalKayaColors.current.accent,
                        contentDescription = page.title,
                        size = 80.dp,
                        iconSize = 36.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Accent-tinted, not ink — this is a brand flourish, not an action.
                    TintPill(text = page.badge, tint = LocalKayaColors.current.accent)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )
                }
            }

            KayaPrimaryButton(
                text = if (currentPage == pages.size - 1) "Get Started" else "Continue",
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_next_button"),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badge: String
)

// ==========================================
// 3. LOGIN SCREEN
// ==========================================
@Composable
fun LoginScreen(
    viewModel: SiteMindViewModel,
    onNavigateRegister: () -> Unit,
    onNavigateForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("john.doe@skylinetower.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val rememberSession by viewModel.rememberSession.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("login_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))

            KayaBrandHero(art = KayaHeroArt.Helmet, size = KayaHeroSize.Small, animated = false)

            Spacer(modifier = Modifier.height(20.dp))

            Text("Welcome back", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displaySmall)
            Text(
                "Sign in to pick up your site where you left off.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            authError?.let { err ->
                ErrorBanner(message = err, modifier = Modifier.padding(bottom = 16.dp))
            }

            Text("Work Email Address", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                placeholder = { Text("engineer@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Password", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                placeholder = { Text("••••••••", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleRememberSession() }
                ) {
                    Checkbox(
                        checked = rememberSession,
                        onCheckedChange = { viewModel.toggleRememberSession() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("login_remember_checkbox")
                    )
                    Text("Remember Session", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                }

                TextButton(
                    onClick = onNavigateForgotPassword,
                    modifier = Modifier.testTag("login_forgot_password_button")
                ) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            KayaPrimaryButton(
                text = "Sign In to Kaya AI",
                onClick = { viewModel.login(email, password) },
                enabled = !authLoading,
                isLoading = authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_submit_button")
            )

            Spacer(modifier = Modifier.height(20.dp))

            KayaSecondaryButton(
                text = "Continue with Google Auth",
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("google_login_button"),
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("New to Kaya AI?", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            TextButton(
                onClick = onNavigateRegister,
                modifier = Modifier.testTag("login_register_link")
            ) {
                Text("Create Account", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ==========================================
// 4. REGISTER SCREEN
// ==========================================
@Composable
fun RegisterScreen(
    viewModel: SiteMindViewModel,
    onNavigateLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.SUPERVISOR) }

    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("register_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = onNavigateLogin,
                modifier = Modifier.testTag("register_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Create Account", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineLarge)
            Text(
                "Register your Kaya AI inspector credentials.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            authError?.let { err ->
                ErrorBanner(message = err, modifier = Modifier.padding(bottom = 16.dp))
            }

            Text("Full Name", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input"),
                placeholder = { Text("Jane Smith", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Work Email", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_email_input"),
                placeholder = { Text("j.smith@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Password", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_password_input"),
                placeholder = { Text("At least 6 characters", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text("Default Construction Role", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserRole.values().take(2).forEach { role ->
                    val isSelected = selectedRole == role
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedRole = role }
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, ShapeMedium),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = ShapeMedium
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                role.title,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            KayaPrimaryButton(
                text = "Complete Registration",
                onClick = { viewModel.register(name, email, password, selectedRole) },
                enabled = !authLoading,
                isLoading = authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_submit_button")
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already registered?", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onNavigateLogin) {
                Text("Sign In", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ==========================================
// 5. FORGOT PASSWORD SCREEN
// ==========================================
@Composable
fun ForgotPasswordScreen(
    viewModel: SiteMindViewModel,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("forgot_password_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("forgot_back_button")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Reset Password", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineLarge)
            Text(
                "Enter your registered work email. We will dispatch a 4-digit OTP verification code.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            authError?.let { err ->
                ErrorBanner(message = err, modifier = Modifier.padding(bottom = 16.dp))
            }

            Text("Email Address", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_email_input"),
                placeholder = { Text("engineer@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            KayaPrimaryButton(
                text = "Send Verification Code",
                onClick = { viewModel.sendForgotPassword(email) },
                enabled = !authLoading,
                isLoading = authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_submit_button")
            )
        }

        Text(
            text = "Need assistance? Contact your site administrator.",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

// ==========================================
// 6. OTP SCREEN
// ==========================================
@Composable
fun OtpScreen(
    viewModel: SiteMindViewModel,
    onNavigateBack: () -> Unit
) {
    var otpCode by remember { mutableStateOf("8492") }
    val pendingEmail by viewModel.pendingEmail.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("otp_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("otp_back_button")) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Enter OTP Code", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineLarge)
            Text(
                "A 4-digit code was sent to ${pendingEmail.ifEmpty { "your email" }}.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            authError?.let { err ->
                ErrorBanner(message = err, modifier = Modifier.padding(bottom = 16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until 4) {
                    val char = otpCode.getOrNull(i)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(MaterialTheme.colorScheme.surface, ShapeLarge)
                            .border(
                                width = 1.5.dp,
                                color = if (char.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = ShapeLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 4) otpCode = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input_field"),
                placeholder = { Text("Enter 4-digit code", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = kayaOutlinedTextFieldColors(),
                shape = ShapeMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            KayaPrimaryButton(
                text = "Verify & Continue",
                onClick = { viewModel.verifyOtp(otpCode) },
                enabled = !authLoading,
                isLoading = authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_verify_button")
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Didn't receive code?", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            KayaGhostButton(
                text = "Resend OTP",
                onClick = { viewModel.sendForgotPassword(pendingEmail) }
            )
        }
    }
}

// ==========================================
// 7. ROLE SELECTION SCREEN
// ==========================================
@Composable
fun RoleSelectionScreen(
    viewModel: SiteMindViewModel,
    onRoleConfirmed: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var selectedRole by remember { mutableStateOf(currentUser.role) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("role_selection_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Construction Role", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineLarge)
            Text(
                "Select your primary site duty to tailor AI vision heuristics & HUD warnings.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            UserRole.entries.forEach { role ->
                val isSelected = selectedRole == role
                val icon = when (role) {
                    UserRole.SUPERVISOR -> Icons.Default.Shield
                    UserRole.WORKER -> Icons.Default.Psychology
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { selectedRole = role }
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = ShapeXLarge
                        )
                        .testTag("role_card_${role.name.lowercase()}"),
                    shape = ShapeXLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = role.title,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(role.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                role.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = LocalKayaColors.current.accent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        KayaPrimaryButton(
            text = "Confirm Role & Open Kaya AI",
            onClick = {
                viewModel.selectRole(selectedRole)
                onRoleConfirmed()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("confirm_role_button")
        )
    }
}
