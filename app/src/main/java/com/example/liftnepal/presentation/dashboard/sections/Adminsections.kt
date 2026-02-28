package com.example.liftnepal.presentation.dashboard.sections

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

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

// ─────────────────────────────────────────────────────────────
// USERS SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminUsersSection(viewModel: AuthViewModel) {
    val usersState by viewModel.usersList.collectAsState()
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchAllUsers() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = usersState) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            is Result.Error -> {
                Text(state.message, color = AdminAccent, modifier = Modifier.align(Alignment.Center))
            }
            is Result.Success -> {
                val users = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(AdminBg),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { AdminSectionHeader("All Users", users.size, Icons.Default.People) }
                    items(users) { user ->
                        AdminCardContainer {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedUser = user },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(44.dp).clip(CircleShape).background(AdminAccentSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (user.displayName.ifEmpty { "U" }).first().toString().uppercase(),
                                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                                        )
                                    }
                                    Column {
                                        Text(user.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                                        Text(user.email, fontSize = 11.sp, color = AdminTextSecondary)
                                    }
                                }
                                IconButton(onClick = { userToDelete = user }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AdminAccent, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }

        selectedUser?.let { user ->
            UserDetailDialog(user = user, onDismiss = { selectedUser = null })
        }
        userToDelete?.let { user ->
            DeleteConfirmDialog(
                userName = user.displayName,
                onConfirm = { viewModel.deleteUser(user.uid); userToDelete = null },
                onDismiss = { userToDelete = null }
            )
        }
    }
}

@Composable
fun UserDetailDialog(user: User, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(24.dp),
        title = { Text("User Details", fontWeight = FontWeight.Bold, color = AdminTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailItem("User ID", user.uid)
                DetailItem("Username", user.displayName)
                DetailItem("Email", user.email)
                DetailItem("Phone", user.phoneNumber)
                DetailItem("Joined", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(user.createdAt)))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = AdminAccent, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = AdminTextMuted, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = AdminTextPrimary)
    }
}

@Composable
fun DeleteConfirmDialog(userName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Delete User", fontWeight = FontWeight.Bold, color = AdminTextPrimary) },
        text = { Text("Are you sure you want to delete $userName? This action cannot be undone.", color = AdminTextSecondary) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Delete", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = AdminTextSecondary) }
        }
    )
}

// ─────────────────────────────────────────────────────────────
// RIDES SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminRidesSection() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Rides Management coming soon...", color = AdminTextSecondary)
    }
}

// ─────────────────────────────────────────────────────────────
// VERIFICATION SECTION (ADMIN)
// Reads from verifications/ table, joined with users/ via uid FK
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminVerificationSection(viewModel: AuthViewModel) {
    // allVerifications = List<Pair<User, Verification>>
    val allVerificationsState by viewModel.allVerifications.collectAsState()
    var selectedPair by remember { mutableStateOf<Pair<User, Verification>?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchAllVerifications() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = allVerificationsState) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            is Result.Error -> {
                Text(state.message, color = AdminAccent, modifier = Modifier.align(Alignment.Center))
            }
            is Result.Success -> {
                val list = state.data
                val pendingCount = list.count { it.second.status == "pending" }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(AdminBg),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        AdminSectionHeader("Verifications", pendingCount, Icons.Default.VerifiedUser)
                    }

                    if (list.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = AdminTextMuted, modifier = Modifier.size(48.dp))
                                    Text("No verifications yet", color = AdminTextMuted, fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        items(list) { (user, verification) ->
                            VerificationRequestCard(
                                user = user,
                                verification = verification,
                                onReview = { selectedPair = Pair(user, verification) }
                            )
                        }
                    }
                }
            }
            else -> {}
        }

        // Review dialog
        selectedPair?.let { (user, verification) ->
            VerificationReviewDialog(
                user = user,
                verification = verification,
                onApprove = {
                    viewModel.approveVerification(verification.uid)
                    selectedPair = null
                },
                onReject = {
                    viewModel.rejectVerification(verification.uid)
                    selectedPair = null
                },
                onDismiss = { selectedPair = null }
            )
        }
    }
}

@Composable
fun VerificationRequestCard(
    user: User,
    verification: Verification,
    onReview: () -> Unit
) {
    val (statusColor, statusLabel) = when (verification.status) {
        "pending"  -> Color(0xFFF59E0B) to "Pending"
        "approved" -> Color(0xFF10B981) to "Approved"
        "rejected" -> Color(0xFFEF4444) to "Rejected"
        else       -> AdminTextMuted    to "Unknown"
    }

    AdminCardContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User info (from users/ table via FK join)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(AdminAccentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (user.displayName.ifEmpty { "U" }).first().toString().uppercase(),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                    )
                }
                Column {
                    Text(user.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                    // Verification data from verifications/ table
                    Text("License: ${verification.licenseNumber}", fontSize = 11.sp, color = AdminTextSecondary)
                    Text("Expiry: ${verification.licenseExpiryDate}", fontSize = 11.sp, color = AdminTextSecondary)
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(statusLabel, statusColor)
                if (verification.status == "pending") {
                    TextButton(
                        onClick = onReview,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Review", color = AdminAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationReviewDialog(
    user: User,
    verification: Verification,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(AdminCard)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Review Verification", fontWeight = FontWeight.Bold, color = AdminTextPrimary, fontSize = 18.sp)

            // License photo from Cloudinary URL stored in verifications/ table
            if (verification.licensePhotoUrl.isNotEmpty()) {
                Text("License Photo", fontSize = 12.sp, color = AdminTextMuted, fontWeight = FontWeight.SemiBold)
                AsyncImage(
                    model = verification.licensePhotoUrl,
                    contentDescription = "License Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AdminBg),
                    contentScale = ContentScale.Crop
                )
            }

            // User info (from users/ FK join) + verification data
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailItem("Name", user.displayName)
                DetailItem("Email", user.email)
                DetailItem("License Number", verification.licenseNumber)
                DetailItem("Expiry Date", verification.licenseExpiryDate)
                DetailItem(
                    "Submitted",
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(verification.submittedAt))
                )
            }

            // Approve / Reject buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Reject", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Approve", fontWeight = FontWeight.SemiBold)
                }
            }

            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancel", color = AdminTextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// ISSUES SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminIssuesSection() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Issues & Support coming soon...", color = AdminTextSecondary)
    }
}