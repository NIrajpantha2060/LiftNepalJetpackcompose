package com.example.liftnepal.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    // Animation states
    var logoAlpha by remember { mutableStateOf(0f) }
    var sloganAlpha by remember { mutableStateOf(0f) }
    var showSlogan by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Step 1: Logo fade in (0% to 100%) - 1.5 seconds
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        ) { value, _ ->
            logoAlpha = value
        }

        // Step 2: Wait 0.5 seconds
        delay(500)

        // Step 3: Show slogan with fade animation
        showSlogan = true
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        ) { value, _ ->
            sloganAlpha = value
        }

        // Step 4: Stay for 2 seconds (4 seconds total)
        delay(2000)

        // TODO: Navigate to next screen here
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Logo with fade animation
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Lift Nepal Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(logoAlpha),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Round round moving arrow
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .alpha(logoAlpha),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Slogan text
            if (showSlogan) {
                Text(
                    text = "A Lift When You Need It",
                    modifier = Modifier.alpha(sloganAlpha),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


