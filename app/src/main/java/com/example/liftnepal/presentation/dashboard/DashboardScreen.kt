package com.example.liftnepal.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.components.BottomNavBar
import com.example.liftnepal.presentation.components.BottomNavItem
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    rideViewModel: RideViewModel  // ✅ Add RideViewModel parameter
) {
    var currentRoute by remember { mutableStateOf("rides") }

    // ✅ Use userData from Firebase Realtime DB (has displayName)
    // NOT currentUser from Firebase Auth (displayName is empty there)
    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data

    LaunchedEffect(Unit) {
        authViewModel.fetchCurrentUserData()
    }

    // Use userData.displayName — falls back to email initial if still loading
    val displayName = userData?.displayName ?: ""
    val userEmail   = userData?.email ?: ""
    val profilePhotoUrl = userData?.profilePhotoUrl ?: ""

    val bottomNavItems = listOf(
        BottomNavItem("Rides",    Icons.Default.Home,      "rides"),
        BottomNavItem("Bookings", Icons.Default.DateRange, "bookings"),
        BottomNavItem("Issues",   Icons.Default.Warning,   "issues"),
        BottomNavItem("Menu",     Icons.Default.Menu,      "menu")
    )

    Scaffold(
        containerColor = SurfaceVariant,
        topBar = {
            UserTopBar(
                currentRoute    = currentRoute,
                userName        = displayName,
                profilePhotoUrl = profilePhotoUrl
            )
        },
        bottomBar = {
            BottomNavBar(
                items          = bottomNavItems,
                currentRoute   = currentRoute,
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
                    "rides"    -> RidesSection(rideViewModel = rideViewModel)
                    "bookings" -> BookingsSection()
                    "issues"   -> IssueSection()
                    "menu"     -> MenuSection(
                        userName        = displayName,   // ✅ from DB
                        userEmail       = userEmail,     // ✅ from DB
                        userData        = userData,
                        viewModel       = authViewModel,
                        onLogout        = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        },
                        onSwitchToRider = {
                            navController.navigate("rider_dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserTopBar(
    currentRoute: String,
    userName: String,
    profilePhotoUrl: String
) {
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
                        Text(
                            // Show name, or "..." while loading
                            text = userName.ifEmpty { "..." },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
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
                    // Notification bell
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = PrimaryColor, modifier = Modifier.size(22.dp))
                    }

                    // Profile avatar — real photo or initial letter
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profilePhotoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                // ✅ Initial letter from DB display name
                                text = userName.firstOrNull()?.toString() ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
        }
    }
}