package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.BorderDark
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.MetaCyan
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
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
            .padding(24.dp)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glasses & AI Symbol
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(MetaBlue.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(1.dp, MetaBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "SiteMind AI",
                    tint = MetaBlue,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SiteMind",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 1.sp
            )

            Text(
                text = "Ray-Ban Meta Glasses Construction AI",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = MetaBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Initializing AI Vision & BLE Pairing...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bottom Footer
        Text(
            text = "Powered by Meta AI & Google Firebase",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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

    val pages = listOf(
        OnboardingPageData(
            title = "Ray-Ban Meta AI Vision",
            description = "Hands-free real-time site inspection powered by vision models. Identify PPE violations and active hazards instantly.",
            icon = Icons.Default.Psychology,
            badge = "Hands-Free Safety"
        ),
        OnboardingPageData(
            title = "Spatial CAD & BIM Overlay",
            description = "Align blueprints with actual physical structural framing in real-time. Detect spatial clashes directly in your field of view.",
            icon = Icons.Default.Architecture,
            badge = "AR Blueprint Verification"
        ),
        OnboardingPageData(
            title = "Automated Shift Reports",
            description = "Speak voice queries to log crew counts, hazard tickets, and daily logs effortlessly with instant RAG SOP lookup.",
            icon = Icons.Default.Assignment,
            badge = "Voice RAG Intelligence"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
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
                        .background(StatusSuccess, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SiteMind Active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusSuccess
                )
            }

            TextButton(
                onClick = onSkip,
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Page Content Card
        val page = pages[currentPage]
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MetaBlue.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, MetaBlue.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = page.title,
                            tint = MetaBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .background(MetaBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = page.badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetaBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Bottom Indicators & Controls
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
                            .background(if (index == currentPage) MetaBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )
                }
            }

            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
            ) {
                Text(
                    text = if (currentPage == pages.size - 1) "Get Started" else "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("login_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(MetaBlue.copy(0.2f), CircleShape)
                        .border(1.dp, MetaBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Logo",
                        tint = MetaBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("SiteMind AI", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Ray-Ban Meta Connect", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text("Welcome Back", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(
                "Sign in to pair your Ray-Ban glasses & inspect sites.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error Banner
            authError?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(StatusError.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, StatusError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(err, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Email Input
            Text("Work Email Address", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                placeholder = { Text("engineer@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MetaBlue) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Text("Password", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                placeholder = { Text("••••••••", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MetaBlue) },
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember & Forgot Password Row
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
                            checkedColor = MetaBlue,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("login_remember_checkbox")
                    )
                    Text("Remember Session", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                }

                TextButton(
                    onClick = onNavigateForgotPassword,
                    modifier = Modifier.testTag("login_forgot_password_button")
                ) {
                    Text("Forgot Password?", color = MetaBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = !authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
            ) {
                if (authLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Sign In to SiteMind", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign In Option
            OutlinedButton(
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_login_button"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFFEA4335), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Continue with Google Auth", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        // Footer Register Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("New to SiteMind?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            TextButton(
                onClick = onNavigateRegister,
                modifier = Modifier.testTag("login_register_link")
            ) {
                Text("Create Account", color = MetaBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

            Text("Create Account", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(
                "Register your SiteMind inspector credentials.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            authError?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(StatusError.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, StatusError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(err, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Name
            Text("Full Name", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input"),
                placeholder = { Text("Jane Smith", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MetaBlue) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Email
            Text("Work Email", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_email_input"),
                placeholder = { Text("j.smith@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MetaBlue) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password
            Text("Password", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_password_input"),
                placeholder = { Text("At least 6 characters", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MetaBlue) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Initial Role Choice
            Text("Default Construction Role", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                            .border(1.dp, if (isSelected) MetaBlue else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MetaBlue.copy(0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(role.title, color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.register(name, email, password, selectedRole) },
                enabled = !authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
            ) {
                if (authLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Complete Registration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already registered?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            TextButton(onClick = onNavigateLogin) {
                Text("Sign In", color = MetaBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

            Text("Reset Password", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(
                "Enter your registered work email. We will dispatch a 4-digit OTP verification code.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            authError?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(StatusError.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, StatusError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(err, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Text("Email Address", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_email_input"),
                placeholder = { Text("engineer@construction.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MetaBlue) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.sendForgotPassword(email) },
                enabled = !authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("forgot_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
            ) {
                if (authLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Send Verification Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Text(
            text = "Need assistance? Contact your site administrator.",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
            fontSize = 11.sp,
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

            Text("Enter OTP Code", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(
                "A 4-digit code was sent to ${pendingEmail.ifEmpty { "your email" }}.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            authError?.let { err ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(StatusError.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, StatusError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(err, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Digit boxes visual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until 4) {
                    val char = otpCode.getOrNull(i)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(
                                width = 1.5.dp,
                                color = if (char.isNotEmpty()) MetaBlue else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
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
                placeholder = { Text("Enter 4-digit code", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MetaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.verifyOtp(otpCode) },
                enabled = !authLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("otp_verify_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
            ) {
                if (authLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Verify & Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Didn't receive code?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            TextButton(onClick = { viewModel.sendForgotPassword(pendingEmail) }) {
                Text("Resend OTP", color = MetaBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .testTag("role_selection_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Construction Role", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(
                "Select your primary site duty to tailor AI vision heuristics & HUD warnings.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
                lineHeight = 18.sp
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
                            color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .testTag("role_card_${role.name.lowercase()}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MetaBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
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
                                .background(if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant, CircleShape),
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
                            Text(role.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                role.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp),
                                lineHeight = 15.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MetaBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.selectRole(selectedRole)
                onRoleConfirmed()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 8.dp)
                .testTag("confirm_role_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue)
        ) {
            Text("Confirm Role & Open SiteMind", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
