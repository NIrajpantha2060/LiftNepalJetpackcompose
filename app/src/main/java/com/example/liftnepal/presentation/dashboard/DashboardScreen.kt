package com.example.liftnepal.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.components.BottomNavBar
import com.example.liftnepal.presentation.components.BottomNavItem
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.NotificationViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    rideViewModel: RideViewModel,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    var currentRoute by remember { mutableStateOf("rides") }
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
        BottomNavItem("Rides",    Icons.Default.Home,    "rides"),
        BottomNavItem("Bookings", Icons.Default.History, "bookings"),
        BottomNavItem("Issues",   Icons.Default.Warning, "issues"),
        BottomNavItem("Menu",     Icons.Default.Menu,    "menu")
    )

    Scaffold(
        containerColor = SurfaceVariant,
        topBar = {
            UserTopBar(
                currentRoute    = currentRoute,
                userName        = displayName,
                profilePhotoUrl = profilePhotoUrl,
                unreadCount     = unreadCount,
                onNotifClick    = { showNotifDialog = true }
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = { (fadeIn() + slideInHorizontally()).togetherWith(fadeOut() + slideOutHorizontally()) },
                label = "dashboard_section"
            ) { route ->
                when (route) {
                    "rides"    -> RidesSection(rideViewModel = rideViewModel, authViewModel = authViewModel)
                    "bookings" -> BookingsSection(rideViewModel = rideViewModel, authViewModel = authViewModel)
                    "issues"   -> IssueSection()
                    "menu"     -> MenuSection(
                        userName        = displayName,
                        userEmail       = userEmail,
                        userData        = userData,
                        viewModel       = authViewModel,
                        onLogout        = {
                            authViewModel.logout()
                            navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                        },
                        onSwitchToRider = {
                            navController.navigate("rider_dashboard") { popUpTo("dashboard") { inclusive = true } }
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
// TOP BAR
// ─────────────────────────────────────────────────────────────

@Composable
fun UserTopBar(
    currentRoute: String,
    userName: String,
    profilePhotoUrl: String,
    unreadCount: Int,
    onNotifClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().background(CardBackground)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left title
                if (currentRoute == "rides") {
                    Column {
                        Text("Hello,", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = userName.ifEmpty { "..." },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                } else {
                    Text(
                        text = when (currentRoute) {
                            "bookings" -> "Booking History"
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
                    // ── Notification bell with green dot outside ──
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .clickable { onNotifClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = PrimaryColor,
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
                            .background(PrimaryColor),
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
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// NOTIFICATION DIALOG
// ─────────────────────────────────────────────────────────────

@Composable
fun NotificationDialog(
    viewModel: NotificationViewModel,
    userId: String,
    onDismiss: () -> Unit
) {
    val notifState by viewModel.notifications.collectAsState()
    val notifications = (notifState as? Result.Success)?.data ?: emptyList()
    val hasUnread = notifications.any { !it.isRead }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 560.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // ── Header row ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // ── Mark all as read ──
                if (hasUnread) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.markAllAsRead(userId) }) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Mark all as read",
                                fontSize = 12.sp,
                                color = PrimaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(10.dp))

                // ── Content ──
                when (val state = notifState) {
                    is Result.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                        }
                    }
                    is Result.Success -> {
                        val list = state.data
                        if (list.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.NotificationsNone,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text("No notifications yet", color = TextSecondary, fontSize = 14.sp)
                                }
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(list) { notif ->
                                    NotificationItem(notif) {
                                        viewModel.markAsRead(userId, notif.id)
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// NOTIFICATION ITEM
// ─────────────────────────────────────────────────────────────

@Composable
fun NotificationItem(notification: Notification, onRead: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!notification.isRead) onRead() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                SurfaceVariant
            else
                PrimaryColor.copy(alpha = 0.07f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (notification.isRead) Color.Transparent else Color(0xFF22C55E),
                        CircleShape
                    )
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    notification.message,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            if (!notification.isRead) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("New", fontSize = 10.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}