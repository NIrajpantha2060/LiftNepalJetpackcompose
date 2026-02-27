package com.example.liftnepal.presentation.dashboard.sections



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.liftnepal.ui.theme.*

@Composable
fun RiderMenuSection(
    userName: String = "John Doe",
    userEmail: String = "john@liftnepal.com",
    onSwitchToUser: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showIssueHistory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiderBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(RiderPrimary.copy(alpha = 0.12f))
                        .border(3.dp, RiderPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = RiderPrimary, modifier = Modifier.size(46.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, fontSize = 13.sp, color = RiderTextSecondary)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Rider badge
                    Box(
                        modifier = Modifier
                            .background(RiderPrimary.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TwoWheeler, null, tint = RiderPrimary, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(5.dp))
                            Text("Rider", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    // Online status badge
                    Box(
                        modifier = Modifier
                            .background(RiderOnlineBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(RiderOnlineGreen, CircleShape)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("Online", fontSize = 13.sp, color = RiderOnlineGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // Support section
        RiderSectionLabel("Support")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                RiderMenuRow(
                    icon = Icons.Default.History,
                    iconColor = RiderSecondary,
                    label = "Issue History",
                    subtitle = "View your submitted issues",
                    onClick = { showIssueHistory = true }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Mode section
        RiderSectionLabel("Mode")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchToUser() }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(AccentGreen.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SwitchAccount, null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Switch to User Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = RiderTextPrimary)
                    Text("Go back to passenger mode", fontSize = 12.sp, color = RiderTextSecondary)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowRight, null, tint = RiderUnselectedNav, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Session
        RiderSectionLabel("Session")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.07f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(AccentRed.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ExitToApp, null, tint = AccentRed, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AccentRed)
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // Issue History Dialog
    if (showIssueHistory) {
        AlertDialog(
            onDismissRequest = { showIssueHistory = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = RiderCardBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = RiderSecondary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Issue History", fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    IssueHistoryItem("App Bug", "Feb 23, 2026", "Resolved")
                    IssueHistoryItem("Payment Issue", "Feb 18, 2026", "Pending")
                    IssueHistoryItem("Technical Problem", "Feb 10, 2026", "Resolved")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showIssueHistory = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary)
                ) { Text("Close", color = Color.White) }
            }
        )
    }
}

@Composable
fun RiderMenuRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = RiderTextPrimary)
                Text(subtitle, fontSize = 12.sp, color = RiderTextSecondary)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = RiderUnselectedNav, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun IssueHistoryItem(category: String, date: String, status: String) {
    val isResolved = status == "Resolved"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RiderSurface, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(category, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RiderTextPrimary)
            Text(date, fontSize = 12.sp, color = RiderTextSecondary)
        }
        Box(
            modifier = Modifier
                .background(
                    if (isResolved) RiderOnlineBg else AccentOrange.copy(alpha = 0.1f),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                status,
                fontSize = 11.sp,
                color = if (isResolved) RiderOnlineGreen else AccentOrange,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}