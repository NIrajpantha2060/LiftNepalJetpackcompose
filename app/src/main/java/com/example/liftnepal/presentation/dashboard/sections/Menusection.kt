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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*

@Composable
fun MenuSection(
    userName: String = "John Doe",
    userEmail: String = "john@liftnepal.com",
    onLogout: () -> Unit = {},
    onSwitchToRider: () -> Unit = {}
) {
    var isSwitchedToRider by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showVerifyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.12f))
                        .border(3.dp, PrimaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = PrimaryColor, modifier = Modifier.size(46.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Passenger", fontSize = 13.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }
                    // Unverified badge
                    Box(
                        modifier = Modifier
                            .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = AccentOrange, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Unverified", fontSize = 13.sp, color = AccentOrange, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // Account section
        MenuSectionLabel("Account")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                MenuRow(Icons.Default.Person,       PrimaryColor,     "My Profile",             "View your account info",    onClick = { showProfileDialog = true })
                RowDivider()
                MenuRow(Icons.Default.Lock,         AccentOrange,     "Change Password",        "Update your password",      onClick = { showChangePasswordDialog = true })
                RowDivider()
                MenuRow(Icons.Default.AccountCircle,AccentDarkBlue,   "Update Profile Picture", "Change your avatar",        onClick = { })
            }
        }

        Spacer(Modifier.height(16.dp))

        // Verify section
        MenuSectionLabel("Verification")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            // Verify Yourself row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showVerifyDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, null, tint = AccentOrange, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Verify Yourself", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                        Text("Required to activate Rider Mode", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Pending", fontSize = 11.sp, color = AccentOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Mode section
        MenuSectionLabel("Mode")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TwoWheeler, null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Switch to Rider Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                        Text(
                            if (isSwitchedToRider) "You are now a Rider" else "Verify yourself first",
                            fontSize = 12.sp, color = TextSecondary
                        )
                    }
                }
                Switch(
                    checked = isSwitchedToRider,
                    onCheckedChange = { isSwitchedToRider = it; if (it) onSwitchToRider() },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryColor)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Session
        MenuSectionLabel("Session")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onLogout() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.07f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
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

    // Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardBackground,
            title = { Text("My Profile", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileDetailRow("Name", userName)
                    ProfileDetailRow("Email", userEmail)
                    ProfileDetailRow("Role", "Passenger")
                    ProfileDetailRow("Member Since", "June 2025")
                    ProfileDetailRow("Verified", "No")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Close", color = Color.White) }
            }
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(onDismiss = { showChangePasswordDialog = false })
    }

    // Verify Yourself Dialog — placeholder UI
    if (showVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = AccentOrange, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verify Yourself", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Info banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AccentOrange.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            "Verification is required to activate Rider Mode. This feature will be available soon.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    // Steps (coming soon placeholders)
                    VerifyStep(number = "1", label = "Upload Government ID",    done = false)
                    VerifyStep(number = "2", label = "Take a Selfie",           done = false)
                    VerifyStep(number = "3", label = "Review & Submit",         done = false)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Coming Soon",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVerifyDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Got it", color = Color.White) }
            }
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────

@Composable
fun VerifyStep(number: String, label: String, done: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (done) AccentGreen else UnselectedNavItem.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            } else {
                Text(number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = if (done) TextPrimary else TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun MenuRow(icon: ImageVector, iconBg: Color, label: String, subtitle: String, onClick: () -> Unit) {
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
                    .background(iconBg.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconBg, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = UnselectedNavItem, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun RowDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = DividerColor, thickness = 0.8.dp)
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun isPasswordStrong(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardBackground,
        title = { Text("Change Password", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (error.isNotEmpty()) {
                    Text(error, color = AccentRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                }

                OutlinedTextField(
                    value = current, onValueChange = { current = it; error = "" },
                    label = { Text("Current Password") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showCurrent = !showCurrent }) { Icon(if (showCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary) } },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryColor, unfocusedBorderColor = DividerColor, cursorColor = PrimaryColor)
                )
                OutlinedTextField(
                    value = newPass, onValueChange = { newPass = it; error = "" },
                    label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showNew = !showNew }) { Icon(if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary) } },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryColor, unfocusedBorderColor = DividerColor, cursorColor = PrimaryColor),
                    supportingText = { Text("Min 8 chars, uppercase, lowercase, number & special char") }
                )
                OutlinedTextField(
                    value = confirm, onValueChange = { confirm = it; error = "" },
                    label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showConfirm = !showConfirm }) { Icon(if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary) } },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryColor, unfocusedBorderColor = DividerColor, cursorColor = PrimaryColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        current.isEmpty() || newPass.isEmpty() || confirm.isEmpty() -> {
                            error = "Please fill all fields"
                        }
                        newPass != confirm -> {
                            error = "New passwords do not match"
                        }
                        !isPasswordStrong(newPass) -> {
                            error = "New password is too weak"
                        }
                        else -> {
                            // Logic to update password would go here
                            onDismiss()
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Update", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) { Text("Cancel", color = TextSecondary) }
        }
    )
}
