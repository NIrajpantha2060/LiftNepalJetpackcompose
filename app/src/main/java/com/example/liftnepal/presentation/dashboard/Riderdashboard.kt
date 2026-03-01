package com.example.liftnepal.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.components.BottomNavItem
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.NotificationViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RiderDashboard(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    rideViewModel: RideViewModel,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    var currentRoute by remember { mutableStateOf("add_ride") }
    var showNotifDialog by remember { mutableStateOf(false) }

    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data

    val notifState by notificationViewModel.notifications.collectAsState()
    val unreadCount = (notifState as? Result.Success)?.data?.count { !it.isRead } ?: 0

    // Initial user fetch
    LaunchedEffect(Unit) {
        authViewModel.fetchCurrentUserData()
    }

    // Fetch notifications once UID is known
    LaunchedEffect(userData?.uid) {
        userData?.uid?.let { notificationViewModel.fetchNotifications(it) }
    }

    val displayName = userData?.displayName ?: ""
    val userEmail = userData?.email ?: ""
    val profilePhotoUrl = userData?.profilePhotoUrl ?: ""

    val bottomNavItems = listOf(
        BottomNavItem("Add Ride", Icons.Default.Add,           "add_ride"),
        BottomNavItem("History",  Icons.Default.History,       "ride_history"),
        BottomNavItem("Issues",   Icons.Default.ReportProblem, "rider_issues"),
        BottomNavItem("Menu",     Icons.Default.Menu,          "rider_menu")
    )

    Scaffold(
        containerColor = RiderBackground,
        topBar = {
            RiderTopBar(
                currentRoute    = currentRoute,
                userName        = displayName,
                profilePhotoUrl = profilePhotoUrl,
                unreadCount     = unreadCount,
                onNotifClick    = { showNotifDialog = true }
            )
        },
        bottomBar = {
            RiderBottomNavBar(
                items          = bottomNavItems,
                currentRoute   = currentRoute,
                onItemSelected = { currentRoute = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = { (fadeIn() + slideInHorizontally()).togetherWith(fadeOut() + slideOutHorizontally()) },
                label = "rider_section"
            ) { route ->
                when (route) {
                    "add_ride"     -> AddRideSection(authViewModel = authViewModel, rideViewModel = rideViewModel)
                    "ride_history" -> RideHistorySection(rideViewModel = rideViewModel, authViewModel = authViewModel)
                    "rider_issues" -> RiderIssueSection()
                    "rider_menu"   -> RiderMenuSection(
                        userName        = displayName,
                        userEmail       = userEmail,
                        authViewModel   = authViewModel,
                        onSwitchToUser  = {
                            navController.navigate("dashboard") { popUpTo("rider_dashboard") { inclusive = true } }
                        },
                        onLogout        = {
                            authViewModel.logout()
                            navController.navigate("login") { popUpTo("rider_dashboard") { inclusive = true } }
                        }
                    )
                }
            }
        }
    }

    if (showNotifDialog && userData != null) {
        NotificationDialog(
            viewModel = notificationViewModel,
            userId    = userData.uid,
            onDismiss = { showNotifDialog = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────
// RIDER TOP BAR
// ─────────────────────────────────────────────────────────────

@Composable
fun RiderTopBar(
    currentRoute: String,
    userName: String,
    profilePhotoUrl: String,
    unreadCount: Int,
    onNotifClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().background(RiderCardBackground)) {
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
                        Text(
                            userName.ifEmpty { "..." },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = RiderTextPrimary
                        )
                    }
                } else {
                    Text(
                        text = when (currentRoute) {
                            "ride_history" -> "Ride History"
                            "rider_issues" -> "Report Issue"
                            "rider_menu"   -> "Menu"
                            else           -> "Rider Dashboard"
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
                    // ── Notification bell with green dot outside ──
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(RiderPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .clickable { onNotifClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = RiderPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Green dot — disappears when unreadCount == 0
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    // Profile avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RiderPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profilePhotoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.firstOrNull()?.toString() ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Rider online status bar
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

// ─────────────────────────────────────────────────────────────
// RIDER BOTTOM NAV BAR
// ─────────────────────────────────────────────────────────────

@Composable
fun RiderBottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(containerColor = RiderCardBackground, tonalElevation = 0.dp) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected  = selected,
                onClick   = { onItemSelected(item.route) },
                icon      = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(22.dp)) },
                label     = {
                    Text(
                        item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = RiderSelectedNav,
                    selectedTextColor   = RiderSelectedNav,
                    unselectedIconColor = RiderUnselectedNav,
                    unselectedTextColor = RiderUnselectedNav,
                    indicatorColor      = RiderPrimary.copy(alpha = 0.12f)
                )
            )
        }
    }
}