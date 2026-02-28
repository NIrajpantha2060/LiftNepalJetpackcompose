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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.data.model.User
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

    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = usersState) {
            is Result.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AdminAccent
                )
            }
            is Result.Error -> {
                Text(
                    text = state.message,
                    color = AdminAccent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is Result.Success -> {
                val users = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminBg),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { AdminSectionHeader("All Users", users.size, Icons.Default.People) }
                    items(users) { user ->
                        AdminCardContainer {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedUser = user },
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
                                            (user.displayName.ifEmpty { "U" }).first().toString().uppercase(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminAccent
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

        // Details Dialog
        selectedUser?.let { user ->
            UserDetailDialog(
                user = user,
                onDismiss = { selectedUser = null }
            )
        }

        // Delete Confirmation Dialog
        userToDelete?.let { user ->
            DeleteConfirmDialog(
                userName = user.displayName,
                onConfirm = {
                    viewModel.deleteUser(user.uid)
                    userToDelete = null
                },
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
        title = {
            Text("User Details", fontWeight = FontWeight.Bold, color = AdminTextPrimary)
        },
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
            TextButton(onClick = onDismiss) {
                Text("Close", color = AdminAccent, fontWeight = FontWeight.Bold)
            }
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
        title = {
            Text("Delete User", fontWeight = FontWeight.Bold, color = AdminTextPrimary)
        },
        text = {
            Text("Are you sure you want to delete $userName? This action cannot be undone.", color = AdminTextSecondary)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AdminTextSecondary)
            }
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
// VERIFICATION SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminVerificationSection() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verification Center coming soon...", color = AdminTextSecondary)
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
