package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*

// ─────────────────────────────────────────────────────────────
// Shared Admin UI Components
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminSectionHeader(title: String, count: Int, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AdminAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AdminAccent, modifier = Modifier.size(18.dp))
            }
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AdminCard)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("$count total", fontSize = 11.sp, color = AdminTextSecondary)
        }
    }
}

@Composable
fun AdminCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AdminCard)
            .border(0.8.dp, AdminBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SmallActionButton(label: String, color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 13.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────────────────────
// USERS SECTION
// ─────────────────────────────────────────────────────────────

data class AdminUser(
    val name: String,
    val email: String,
    val phone: String,
    val status: String,
    val joinDate: String
)

val sampleUsers = listOf(
    AdminUser("Rohan Sharma",  "rohan@gmail.com",  "+977 9841000001", "Active",   "Jan 12, 2025"),
    AdminUser("Priya Thapa",   "priya@gmail.com",  "+977 9841000002", "Active",   "Jan 18, 2025"),
    AdminUser("Suman Rai",     "suman@gmail.com",  "+977 9841000003", "Inactive", "Feb 02, 2025"),
    AdminUser("Anisha Gurung", "anisha@gmail.com", "+977 9841000004", "Active",   "Feb 14, 2025"),
    AdminUser("Bikash Pandey", "bikash@gmail.com", "+977 9841000005", "Banned",   "Mar 01, 2025"),
)

@Composable
fun AdminUsersSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBg),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { AdminSectionHeader("All Users", sampleUsers.size, Icons.Default.People) }
        items(sampleUsers) { user ->
            AdminCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AdminAccentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.name.first().toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminAccent
                            )
                        }
                        Column {
                            Text(user.name,  fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                            Text(user.email, fontSize = 11.sp, color = AdminTextSecondary)
                            Text(user.phone, fontSize = 11.sp, color = AdminTextMuted)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        StatusBadge(
                            label = user.status,
                            color = when (user.status) {
                                "Active"   -> Color(0xFF27AE60)
                                "Inactive" -> Color(0xFFF39C12)
                                "Banned"   -> AdminAccent
                                else       -> AdminTextSecondary
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(user.joinDate, fontSize = 10.sp, color = AdminTextMuted)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RIDES SECTION
// ─────────────────────────────────────────────────────────────

data class AdminRide(
    val rideId: String,
    val rider: String,
    val from: String,
    val to: String,
    val date: String,
    val status: String,
    val fare: String
)

val sampleRides = listOf(
    AdminRide("RD-001", "Rohan Sharma",  "Thamel",      "Patan",         "Mar 10, 2025", "Completed", "Rs 250"),
    AdminRide("RD-002", "Priya Thapa",   "Baneshwor",   "Kalanki",       "Mar 12, 2025", "Completed", "Rs 180"),
    AdminRide("RD-003", "Suman Rai",     "Koteshwor",   "New Baneshwor", "Mar 15, 2025", "Cancelled", "Rs 0"),
    AdminRide("RD-004", "Anisha Gurung", "Maharajgunj", "Lalitpur",      "Mar 20, 2025", "Ongoing",   "Rs 320"),
    AdminRide("RD-005", "Bikash Pandey", "Gongabu",     "Chabahil",      "Mar 22, 2025", "Completed", "Rs 140"),
)

@Composable
fun AdminRidesSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBg),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { AdminSectionHeader("All Rides", sampleRides.size, Icons.Default.DirectionsCar) }
        items(sampleRides) { ride ->
            AdminCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ride.rideId, fontSize = 11.sp, color = AdminAccent, fontWeight = FontWeight.Bold)
                            Text("·", color = AdminTextMuted)
                            Text(ride.rider,  fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.LocationOn,  null, tint = AdminAccent,    modifier = Modifier.size(13.dp))
                            Text(ride.from, fontSize = 12.sp, color = AdminTextSecondary)
                            Icon(Icons.Default.ArrowForward, null, tint = AdminTextMuted, modifier = Modifier.size(12.dp))
                            Text(ride.to,   fontSize = 12.sp, color = AdminTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ride.date, fontSize = 11.sp, color = AdminTextMuted)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusBadge(
                            label = ride.status,
                            color = when (ride.status) {
                                "Completed" -> Color(0xFF27AE60)
                                "Ongoing"   -> Color(0xFF037DC0)
                                "Cancelled" -> AdminAccent
                                else        -> AdminTextSecondary
                            }
                        )
                        Text(ride.fare, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// VERIFICATION SECTION
// ─────────────────────────────────────────────────────────────

data class VerificationRequest(
    val name: String,
    val docType: String,
    val submitted: String,
    val status: String
)

val sampleVerifications = listOf(
    VerificationRequest("Rohan Sharma",  "Citizenship + License", "Mar 08, 2025", "Pending"),
    VerificationRequest("Suman Rai",     "Citizenship",           "Mar 11, 2025", "Approved"),
    VerificationRequest("Bikash Pandey", "License",               "Mar 14, 2025", "Rejected"),
    VerificationRequest("Nita Karki",    "Citizenship + License", "Mar 18, 2025", "Pending"),
    VerificationRequest("Dev Shrestha",  "Citizenship",           "Mar 21, 2025", "Pending"),
)

@Composable
fun AdminVerificationSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBg),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { AdminSectionHeader("Verification Requests", sampleVerifications.size, Icons.Default.VerifiedUser) }
        items(sampleVerifications) { req ->
            AdminCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AdminBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, null, tint = AdminAccent, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text(req.name,    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                            Text(req.docType, fontSize = 11.sp, color = AdminTextSecondary)
                            Text("Submitted: ${req.submitted}", fontSize = 10.sp, color = AdminTextMuted)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusBadge(
                            label = req.status,
                            color = when (req.status) {
                                "Approved" -> Color(0xFF27AE60)
                                "Pending"  -> Color(0xFFF39C12)
                                "Rejected" -> AdminAccent
                                else       -> AdminTextSecondary
                            }
                        )
                        if (req.status == "Pending") {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SmallActionButton("✓", Color(0xFF27AE60))
                                SmallActionButton("✗", AdminAccent)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// ISSUES SECTION
// ─────────────────────────────────────────────────────────────

data class AdminIssue(
    val issueId: String,
    val reporter: String,
    val subject: String,
    val description: String,
    val date: String,
    val priority: String,
    val status: String
)

val sampleIssues = listOf(
    AdminIssue("IS-001", "Priya Thapa",   "Driver was rude",        "The driver was very rude during the trip.",    "Mar 09, 2025", "High",   "Open"),
    AdminIssue("IS-002", "Rohan Sharma",  "App crashed",            "App crashes when I try to book a ride.",       "Mar 13, 2025", "Medium", "In Review"),
    AdminIssue("IS-003", "Suman Rai",     "Wrong fare charged",     "I was charged Rs 100 extra for a short ride.", "Mar 16, 2025", "High",   "Resolved"),
    AdminIssue("IS-004", "Anisha Gurung", "Ride cancelled unfairly","Driver cancelled without reason mid ride.",    "Mar 19, 2025", "Medium", "Open"),
    AdminIssue("IS-005", "Dev Shrestha",  "Payment failed",         "Payment deducted but ride not confirmed.",     "Mar 22, 2025", "High",   "In Review"),
)

@Composable
fun AdminIssuesSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBg),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { AdminSectionHeader("Reported Issues", sampleIssues.size, Icons.Default.BugReport) }
        items(sampleIssues) { issue ->
            AdminCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(issue.issueId,  fontSize = 11.sp, color = AdminAccent, fontWeight = FontWeight.Bold)
                            Text("·", color = AdminTextMuted)
                            Text(issue.reporter, fontSize = 12.sp, color = AdminTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(issue.subject,     fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(issue.description, fontSize = 12.sp, color = AdminTextSecondary, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(issue.date,        fontSize = 10.sp, color = AdminTextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusBadge(
                            label = issue.priority,
                            color = when (issue.priority) {
                                "High"   -> AdminAccent
                                "Medium" -> Color(0xFFF39C12)
                                "Low"    -> Color(0xFF27AE60)
                                else     -> AdminTextSecondary
                            }
                        )
                        StatusBadge(
                            label = issue.status,
                            color = when (issue.status) {
                                "Open"      -> Color(0xFF037DC0)
                                "In Review" -> Color(0xFFF39C12)
                                "Resolved"  -> Color(0xFF27AE60)
                                else        -> AdminTextSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}