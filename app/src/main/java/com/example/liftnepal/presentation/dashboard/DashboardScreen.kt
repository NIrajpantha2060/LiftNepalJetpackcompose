package com.example.liftnepal.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.liftnepal.presentation.components.BottomNavBar
import com.example.liftnepal.presentation.components.BottomNavItem
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var currentRoute by remember { mutableStateOf("rides") }

    val bottomNavItems = listOf(
        BottomNavItem("Rides",    Icons.Default.Home,      "rides"),
        BottomNavItem("Bookings", Icons.Default.DateRange, "bookings"),
        BottomNavItem("Issues",   Icons.Default.Warning,   "issues"),
        BottomNavItem("Menu",     Icons.Default.Menu,      "menu")
    )

    Scaffold(
        containerColor = SurfaceVariant,
        topBar = { UserTopBar(currentRoute = currentRoute) },
        bottomBar = {
            BottomNavBar(
                items = bottomNavItems,
                currentRoute = currentRoute,
                onItemSelected = { currentRoute = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally()).togetherWith(fadeOut() + slideOutHorizontally())
                },
                label = "dashboard_section"
            ) { route ->
                when (route) {
                    "rides"    -> RidesSection()
                    "bookings" -> BookingsSection()
                    "issues"   -> IssueSection()
                    "menu"     -> MenuSection(
                        userName = "John Doe",
                        userEmail = "john@liftnepal.com",
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        },
                        onSwitchToRider = { /* wire later */ }
                    )
                }
            }
        }
    }
}

@Composable
fun UserTopBar(currentRoute: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentRoute == "rides") {
                    Column {
                        Text("Hello,", fontSize = 13.sp, color = TextSecondary)
                        Text("John Doe", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                } else {
                    Text(
                        text = when (currentRoute) {
                            "bookings" -> "My Bookings"
                            "issues"   -> "Report Issue"
                            "menu"     -> "Menu"
                            else       -> "Dashboard"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = PrimaryColor, modifier = Modifier.size(22.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("J", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
        }
    }
}