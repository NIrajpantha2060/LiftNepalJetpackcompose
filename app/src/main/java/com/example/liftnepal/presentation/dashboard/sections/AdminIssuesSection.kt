package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Issue
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.IssueViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminIssuesSection(issueViewModel: IssueViewModel) {

    val allIssuesState by issueViewModel.allIssuesState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var selectedIssue by remember { mutableStateOf<Issue?>(null) }

    LaunchedEffect(Unit) { issueViewModel.fetchAllIssues() }

    val allIssues = (allIssuesState as? Result.Success)?.data ?: emptyList()
    val userIssues  = allIssues.filter { it.userType == "user" }
    val riderIssues = allIssues.filter { it.userType == "rider" }
    val displayList = if (selectedTab == 0) userIssues else riderIssues

    Box(modifier = Modifier.fillMaxSize().background(AdminBg)) {
        when {
            allIssuesState is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            allIssuesState is Result.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text((allIssuesState as Result.Error).message, color = AdminAccent)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { issueViewModel.fetchAllIssues() },
                        colors = ButtonDefaults.buttonColors(containerColor = AdminAccent)
                    ) { Text("Retry") }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        AdminSectionHeader("Issues & Support", allIssues.size, Icons.Default.BugReport)
                    }

                    // Summary row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AdminCard)
                                .border(0.8.dp, AdminBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AdminIssueStat(allIssues.count { it.status == "open" }.toString(),     "Open",     Color(0xFFEF4444))
                            AdminIssueStat(allIssues.count { it.status == "resolved" }.toString(), "Resolved", Color(0xFF10B981))
                            AdminIssueStat(userIssues.size.toString(),                             "Users",    Color(0xFF3B82F6))
                            AdminIssueStat(riderIssues.size.toString(),                            "Riders",   Color(0xFFF59E0B))
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Tab selector
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AdminCard)
                                .border(0.8.dp, AdminBorder, RoundedCornerShape(14.dp))
                                .padding(4.dp)
                        ) {
                            AdminIssueTab("User Issues",  userIssues.size,  selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                            AdminIssueTab("Rider Issues", riderIssues.size, selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    if (displayList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CheckCircle, null, tint = AdminTextMuted, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("No ${if (selectedTab == 0) "user" else "rider"} issues", color = AdminTextSecondary, fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        items(displayList) { issue ->
                            AdminIssueCard(issue = issue, onClick = { selectedIssue = issue })
                        }
                    }
                }
            }
        }
    }

    selectedIssue?.let { issue ->
        AdminIssueDetailDialog(
            issue = issue,
            onResolve = { remarks ->                          // ✅ receives remarks
                issueViewModel.resolveIssue(issue.issueId, remarks)
                selectedIssue = null
            },
            onDismiss = { selectedIssue = null }
        )
    }
}

// ── Stat box ──────────────────────────────────────────────────

@Composable
fun AdminIssueStat(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = AdminTextSecondary)
    }
}

// ── Tab button ────────────────────────────────────────────────

@Composable
fun AdminIssueTab(
    label: String,
    count: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AdminAccent else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else AdminTextSecondary
            )
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else AdminAccentSoft)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        count.toString(),
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else AdminAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── Issue card ────────────────────────────────────────────────

@Composable
fun AdminIssueCard(issue: Issue, onClick: () -> Unit) {
    val statusColor = if (issue.status == "open") Color(0xFFEF4444) else Color(0xFF10B981)
    val statusLabel = if (issue.status == "open") "Open" else "Resolved"

    AdminCardContainer {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AdminAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                if (issue.userPhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = issue.userPhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        (issue.userName.firstOrNull() ?: "U").toString().uppercase(),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(issue.userName.ifEmpty { "Unknown User" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                Text(issue.userPhone.ifEmpty { "No phone" }, fontSize = 11.sp, color = AdminTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusBadge(statusLabel, statusColor)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AdminAccentSoft).padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(issue.category, fontSize = 10.sp, color = AdminAccent)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = AdminBorder)
        Spacer(Modifier.height(10.dp))

        Text(issue.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(issue.description, fontSize = 12.sp, color = AdminTextSecondary, maxLines = 2)

        // Show remarks preview if resolved
        if (issue.status == "resolved" && issue.adminRemarks.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Comment, null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                Text(
                    issue.adminRemarks,
                    fontSize = 11.sp,
                    color = Color(0xFF10B981),
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(issue.createdAt)),
            fontSize = 10.sp,
            color = AdminTextMuted
        )
    }
}

// ── Detail dialog ─────────────────────────────────────────────

@Composable
fun AdminIssueDetailDialog(
    issue: Issue,
    onResolve: (String) -> Unit,          // ✅ passes remarks string
    onDismiss: () -> Unit
) {
    val statusColor = if (issue.status == "open") Color(0xFFEF4444) else Color(0xFF10B981)
    val statusLabel = if (issue.status == "open") "OPEN" else "RESOLVED"

    var adminRemarks by remember { mutableStateOf("") }  // ✅ remarks state

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(AdminCard)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Issue Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = AdminTextSecondary)
                }
            }

            Spacer(Modifier.height(4.dp))

            // Status badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(statusLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = AdminBorder)
            Spacer(Modifier.height(14.dp))

            // User info
            Text("Raised By", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(AdminAccentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    if (issue.userPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = issue.userPhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            (issue.userName.firstOrNull() ?: "U").toString().uppercase(),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                        )
                    }
                }
                Column {
                    Text(issue.userName.ifEmpty { "Unknown User" }, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AdminTextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Phone, null, tint = AdminAccent, modifier = Modifier.size(13.dp))
                        Text(issue.userPhone.ifEmpty { "No phone" }, fontSize = 12.sp, color = AdminTextSecondary)
                    }
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AdminAccentSoft).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(issue.userType.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, color = AdminAccent, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = AdminBorder)
            Spacer(Modifier.height(14.dp))

            // Issue details
            Text("Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AdminAccentSoft).padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(issue.category, fontSize = 11.sp, color = AdminAccent, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Text(issue.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(issue.description, fontSize = 13.sp, color = AdminTextSecondary, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(issue.createdAt)),
                fontSize = 11.sp,
                color = AdminTextMuted
            )

            // Screenshot
            if (issue.screenshotUrl.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = AdminBorder)
                Spacer(Modifier.height(14.dp))
                Text("Screenshot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = issue.screenshotUrl,
                    contentDescription = "Issue Screenshot",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AdminBg),
                    contentScale = ContentScale.Crop
                )
            }

            // Show existing remarks if already resolved
            if (issue.status == "resolved" && issue.adminRemarks.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = AdminBorder)
                Spacer(Modifier.height(14.dp))
                Text("Admin Remarks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(issue.adminRemarks, fontSize = 13.sp, color = Color(0xFF10B981), lineHeight = 20.sp)
                }
            }

            // ✅ Remarks input — only show when issue is open
            if (issue.status == "open") {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = AdminBorder)
                Spacer(Modifier.height(14.dp))
                Text("Admin Remarks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Optional — will be sent to the user in the notification",
                    fontSize = 11.sp,
                    color = AdminTextMuted
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = adminRemarks,
                    onValueChange = { adminRemarks = it },
                    placeholder = { Text("e.g. We have fixed the issue on our end...", color = AdminTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdminAccent,
                        unfocusedBorderColor = AdminBorder,
                        cursorColor = AdminAccent,
                        focusedLabelColor = AdminAccent
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", color = AdminTextSecondary)
                }

                if (issue.status == "open") {
                    Button(
                        onClick = { onResolve(adminRemarks) },  // ✅ pass remarks
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Mark Resolved", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}