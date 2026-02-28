//
//
//package com.example.liftnepal.presentation.auth
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavHostController
//import com.example.liftnepal.R
//import com.example.liftnepal.data.utils.Result
//import com.example.liftnepal.presentation.viewmodel.AuthViewModel
//
//// Password Visibility Toggle Component
//@Composable
//fun PasswordVisibilityToggle(
//    isVisible: Boolean,
//    onToggle: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    IconButton(
//        onClick = onToggle,
//        modifier = modifier
//    ) {
//        Icon(
//            painter = painterResource(
//                id = if (isVisible) R.drawable.baseline_visibility_24
//                else R.drawable.baseline_visibility_off_24
//            ),
//            contentDescription = if (isVisible) "Hide password" else "Show password",
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginScreen(
//    navController: NavHostController,
//    viewModel: AuthViewModel
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
//
//    // Handle navigation when login succeeds
//    LaunchedEffect(loginState) {
//        if (loginState is Result.Success) {
//            navController.navigate("dashboard") {
//                popUpTo("login") { inclusive = true }
//            }
//            viewModel.clearLoginState()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Logo
//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(250.dp)
//                .padding(top = 60.dp, bottom = 20.dp)
//        )
//
//        // Title
//        Text(
//            text = "Welcome Back",
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        // Subtitle
//        Text(
//            text = "Login to continue",
//            fontSize = 16.sp,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//
//        // Error Message
//        if (loginState is Result.Error) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
//                ),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Text(
//                    text = (loginState as Result.Error).message,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//
//        // Email Field
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = {
//                Text(
//                    "Email Address",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                focusedLabelColor = MaterialTheme.colorScheme.primary
//            ),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Password Field
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = {
//                Text(
//                    "Password",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            },
//            visualTransformation = if (passwordVisible) {
//                VisualTransformation.None
//            } else {
//                PasswordVisualTransformation()
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                focusedLabelColor = MaterialTheme.colorScheme.primary
//            ),
//            trailingIcon = {
//                PasswordVisibilityToggle(
//                    isVisible = passwordVisible,
//                    onToggle = { passwordVisible = !passwordVisible }
//                )
//            },
//            singleLine = true
//        )
//
//        // Forgot Password Link
//        TextButton(
//            onClick = { navController.navigate("forget_password") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp)
//        ) {
//            Text(
//                "Forgot Password?",
//                color = MaterialTheme.colorScheme.primary,
//                fontWeight = FontWeight.Medium
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Login Button
//        Button(
//            onClick = {
//                if (email.isNotEmpty() && password.isNotEmpty()) {
//                    viewModel.login(email, password)
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            shape = RoundedCornerShape(16.dp),
//            enabled = loginState !is Result.Loading,
//            elevation = ButtonDefaults.buttonElevation(
//                defaultElevation = 4.dp,
//                pressedElevation = 8.dp
//            )
//        ) {
//            if (loginState is Result.Loading) {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.onPrimary,
//                    modifier = Modifier.size(24.dp),
//                    strokeWidth = 2.dp
//                )
//            } else {
//                Text(
//                    "Login",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
//
//        // OR Divider
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 24.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Divider(
//                modifier = Modifier.weight(1f),
//                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
//                thickness = 1.dp
//            )
//            Text(
//                text = "OR",
//                modifier = Modifier.padding(horizontal = 16.dp),
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                fontSize = 14.sp
//            )
//            Divider(
//                modifier = Modifier.weight(1f),
//                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
//                thickness = 1.dp
//            )
//        }
//
//        // Sign Up Link
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
////            Text(
////                "Don't have an account? ",
////                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
////                fontSize = 14.sp
////            )
//            TextButton(
//                onClick = { navController.navigate("signup") },
//                modifier = Modifier.padding(start = 4.dp)
//            ) {
//                Text(
//                    "Dont have an account? signup",
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 14.sp
//                )
//            }
//        }
//    }
//}

package com.example.liftnepal.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.liftnepal.R
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel

private const val ADMIN_EMAIL    = "admin@gmail.com"
private const val ADMIN_PASSWORD = "@Admin2060"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var adminError      by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    // Firebase login success → regular dashboard
    LaunchedEffect(loginState) {
        if (loginState is Result.Success) {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
            viewModel.clearLoginState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(250.dp)
                .padding(top = 60.dp, bottom = 20.dp)
        )

        Text(
            text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Login to continue",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Error message
        val errorMessage = when {
            adminError                 -> "Invalid admin credentials."
            loginState is Result.Error -> (loginState as Result.Error).message
            else                       -> null
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; adminError = false },
            label = { Text("Email Address", style = MaterialTheme.typography.bodyMedium) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor  = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; adminError = false },
            label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor  = MaterialTheme.colorScheme.primary
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.baseline_visibility_24
                            else R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            singleLine = true
        )

        TextButton(
            onClick = { navController.navigate("forget_password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button — admin check first, then Firebase
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    when {
                        email.trim() == ADMIN_EMAIL && password == ADMIN_PASSWORD -> {
                            // ✅ Admin — skip Firebase entirely
                            navController.navigate("admin_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        email.trim() == ADMIN_EMAIL -> {
                            // Admin email but wrong password
                            adminError = true
                        }
                        else -> {
                            // Regular user — Firebase login
                            adminError = false
                            viewModel.login(email, password)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = loginState !is Result.Loading,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
        ) {
            if (loginState is Result.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
            Text("OR", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
            Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { navController.navigate("signup") }) {
                Text(
                    "Don't have an account? Sign up",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}