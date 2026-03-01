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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.liftnepal.presentation.dashboard.sections.*
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.IssueViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*

data class AdminNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AdminDashboard(
    navController: NavHostController,
    viewModel: AuthViewModel,
    rideViewModel: RideViewModel,
    issueViewModel: IssueViewModel = viewModel()   // ✅ Added
) {
    var currentRoute by remember { mutableStateOf("users") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        AdminNavItem("Users",        Icons.Default.People,        "users"),
        AdminNavItem("Rides",        Icons.Default.DirectionsCar, "rides"),
        AdminNavItem("Verification", Icons.Default.VerifiedUser,  "verification"),
        AdminNavItem("Issues",       Icons.Default.BugReport,     "issues")
    )

    if (showLogoutDialog) {
        AdminLogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("admin_dashboard") { inclusive = true }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        containerColor = AdminBg,
        topBar = {
            AdminTopBar(
                currentRoute = currentRoute,
                onLogoutClick = { showLogoutDialog = true }
            )
        },
        bottomBar = {
            AdminBottomNav(
                items = navItems,
                currentRoute = currentRoute,
                onItemSelected = { currentRoute = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AdminBg)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally()).togetherWith(fadeOut() + slideOutHorizontally())
                },
                label = "admin_section"
            ) { route ->
                when (route) {
                    "users"        -> AdminUsersSection(viewModel)
                    "rides"        -> AdminRidesSection(rideViewModel)
                    "verification" -> AdminVerificationSection(viewModel)
                    "issues"       -> AdminIssuesSection(issueViewModel)  // ✅ Wired up
                }
            }
        }
    }
}

@Composable
fun AdminTopBar(currentRoute: String, onLogoutClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(AdminSurface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Admin Panel", fontSize = 11.sp, color = AdminAccent, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp)
                Text(
                    text = when (currentRoute) {
                        "users"        -> "Users"
                        "rides"        -> "Rides"
                        "verification" -> "Verification"
                        "issues"       -> "Issues"
                        else           -> "Dashboard"
                    },
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary
                )
            }
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(AdminAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onLogoutClick) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Logout", tint = AdminAccent, modifier = Modifier.size(26.dp))
                }
            }
        }
        HorizontalDivider(color = AdminBorder, thickness = 0.8.dp)
    }
}

@Composable
fun AdminBottomNav(items: List<AdminNavItem>, currentRoute: String, onItemSelected: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(AdminSurface)) {
        HorizontalDivider(color = AdminBorder, thickness = 0.8.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                    IconButton(onClick = { onItemSelected(item.route) }) {
                        Icon(imageVector = item.icon, contentDescription = item.label, tint = if (selected) AdminAccent else AdminTextMuted, modifier = Modifier.size(24.dp))
                    }
                    Text(text = item.label, fontSize = 10.sp, color = if (selected) AdminAccent else AdminTextMuted, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun AdminLogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Logout", color = AdminTextPrimary, fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to logout from the admin panel?", color = AdminTextSecondary) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = AdminAccent), shape = RoundedCornerShape(10.dp)) {
                Text("Logout", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = AdminTextSecondary) }
        }
    )
}