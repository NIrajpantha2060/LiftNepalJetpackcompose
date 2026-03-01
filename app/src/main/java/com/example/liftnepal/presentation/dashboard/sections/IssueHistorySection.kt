package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Issue
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.IssueViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IssueHistorySection(
    issueViewModel: IssueViewModel,
    currentUser: User?,
    isRiderMode: Boolean = false, // ✅ Theme awareness
    onBack: () -> Unit
) {
    val issuesState by issueViewModel.userIssuesState.collectAsState()

    // Colors based on mode
    val bgColor = if (isRiderMode) RiderBackground else SurfaceVariant
    val cardColor = if (isRiderMode) RiderCardBackground else CardBackground
    val primary = if (isRiderMode) RiderPrimary else PrimaryColor
    val textPrimary = if (isRiderMode) RiderTextPrimary else TextPrimary
    val textSecondary = if (isRiderMode) RiderTextSecondary else TextSecondary
    val divider = if (isRiderMode) RiderDivider else DividerColor

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { issueViewModel.fetchUserIssues(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(cardColor, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Issue History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Text("Track your reported issues", fontSize = 12.sp, color = textSecondary)
            }
        }

        when (val state = issuesState) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primary)
                }
            }
            is Result.Success -> {
                val issues = state.data
                if (issues.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = textSecondary.copy(alpha = 0.5f))
                            Spacer(Modifier.height(16.dp))
                            Text("No issues reported yet", color = textSecondary, fontSize = 16.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(issues) { issue ->
                            UserIssueItem(issue, isRiderMode)
                        }
                    }
                }
            }
            is Result.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = AccentRed)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun UserIssueItem(issue: Issue, isRiderMode: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    
    val cardColor = if (isRiderMode) RiderCardBackground else CardBackground
    val primary = if (isRiderMode) RiderPrimary else PrimaryColor
    val textPrimary = if (isRiderMode) RiderTextPrimary else TextPrimary
    val textSecondary = if (isRiderMode) RiderTextSecondary else TextSecondary
    val divider = if (isRiderMode) RiderDivider else DividerColor
    val unselected = if (isRiderMode) RiderUnselectedNav else UnselectedNavItem
    
    val statusColor = if (issue.status == "resolved") AccentGreen else AccentOrange
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = issue.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = issue.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = issue.status.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = dateFormat.format(Date(issue.createdAt)),
                fontSize = 11.sp,
                color = textSecondary
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = divider, thickness = 0.8.dp)
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                    Text(issue.description, fontSize = 14.sp, color = textPrimary)

                    if (issue.screenshotUrl.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Screenshot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Spacer(Modifier.height(8.dp))
                        AsyncImage(
                            model = issue.screenshotUrl,
                            contentDescription = "Issue Screenshot",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (issue.adminRemarks.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(primary.copy(alpha = 0.05f))
                                .border(1.dp, primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, null, tint = primary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Admin Remarks", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primary)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(issue.adminRemarks, fontSize = 13.sp, color = textPrimary)
                            }
                        }
                    } else if (issue.status == "resolved") {
                        Spacer(Modifier.height(16.dp))
                        Text("This issue was resolved by the admin.", fontSize = 13.sp, color = AccentGreen, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, null, tint = unselected, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (!expanded) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = unselected, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}