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
import com.example.liftnepal.presentation.components.BottomNavItem
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RiderDashboard(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    var currentRoute by remember { mutableStateOf("add_ride") }
    val currentUser = viewModel.currentUser

    val bottomNavItems = listOf(
        BottomNavItem("Add Ride",    Icons.Default.Add,        "add_ride"),
        BottomNavItem("History",     Icons.Default.History,     "ride_history"),
        BottomNavItem("Issues",      Icons.Default.ReportProblem, "rider_issues"),
        BottomNavItem("Menu",        Icons.Default.Menu,        "rider_menu")
    )

    Scaffold(
        containerColor = RiderBackground,
        topBar = {
            RiderTopBar(
                currentRoute = currentRoute,
                userName = currentUser?.displayName ?: ""
            )
        },
        bottomBar = {
            RiderBottomNavBar(
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
                label = "rider_section"
            ) { route ->
                when (route) {
                    "add_ride"     -> AddRideSection()
                    "ride_history" -> RideHistorySection()
                    "rider_issues" -> RiderIssueSection()
                    "rider_menu"   -> RiderMenuSection(
                        userName = currentUser?.displayName ?: "User",
                        userEmail = currentUser?.email ?: "",
                        onSwitchToUser = {
                            navController.navigate("dashboard") {
                                popUpTo("rider_dashboard") { inclusive = true }
                            }
                        },
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("rider_dashboard") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ── Rider Top Bar ─────────────────────────────────────────────

@Composable
fun RiderTopBar(currentRoute: String, userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(RiderCardBackground)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentRoute == "add_ride") {
                    Column {
                        Text("Rider Mode", fontSize = 13.sp, color = RiderTextSecondary)
                        Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                    }
                } else {
                    Text(
                        text = when (currentRoute) {
                            "ride_history"  -> "Ride History"
                            "rider_issues"  -> "Report Issue"
                            "rider_menu"    -> "Menu"
                            else            -> "Rider Dashboard"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = RiderTextPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notifications
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(RiderPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = RiderPrimary, modifier = Modifier.size(22.dp))
                    }
                    // Online indicator + Avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(RiderPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                userName.firstOrNull()?.toString() ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        // Green online dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .background(RiderOnlineGreen, RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0f))
                        )
                    }
                }
            }

            // Rider mode banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RiderOnlineBg)
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(RiderOnlineGreen, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Rider Mode Active",
                        fontSize = 12.sp,
                        color = RiderOnlineGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(color = RiderDivider, thickness = 0.8.dp)
        }
    }
}

// ── Rider Bottom Nav Bar ──────────────────────────────────────

@Composable
fun RiderBottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = RiderCardBackground,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(item.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RiderSelectedNav,
                    selectedTextColor = RiderSelectedNav,
                    unselectedIconColor = RiderUnselectedNav,
                    unselectedTextColor = RiderUnselectedNav,
                    indicatorColor = RiderPrimary.copy(alpha = 0.12f)
                )
            )
        }
    }
}