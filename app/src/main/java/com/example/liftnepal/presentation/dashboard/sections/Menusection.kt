package com.example.liftnepal.presentation.dashboard.sections

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.CloudinaryUploader
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MenuSection(
    userName: String = "User",
    userEmail: String = "",
    userData: User? = null,
    onLogout: () -> Unit = {},
    onSwitchToRider: () -> Unit = {},
    onIssueHistoryClick: () -> Unit = {}, // ✅ Added
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    // Observe verification status
    val myVerificationState by viewModel.myVerification.collectAsState()
    val verificationStatus = when (val state = myVerificationState) {
        is Result.Success -> state.data?.status ?: "none"
        else -> "none"
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMyVerification()
    }

    // Profile photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploadingPhoto = true
            scope.launch {
                when (val result = CloudinaryUploader.uploadImage(context, it, CloudinaryUploader.PRESET_PROFILES)) {
                    is Result.Success -> {
                        viewModel.updateProfilePhoto(result.data)
                        isUploadingPhoto = false
                    }
                    is Result.Error -> {
                        isUploadingPhoto = false
                    }
                    else -> { isUploadingPhoto = false }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Profile Header Card ───────────────────────────────────
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
                // Profile photo with upload button overlay
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Profile photo circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.12f))
                            .border(3.dp, PrimaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingPhoto) {
                            CircularProgressIndicator(
                                color = PrimaryColor,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                        } else if (!userData?.profilePhotoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = userData!!.profilePhotoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = PrimaryColor, modifier = Modifier.size(46.dp))
                        }
                    }

                    // Camera button to change photo
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor)
                            .clickable { showPhotoOptions = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )

                        DropdownMenu(
                            expanded = showPhotoOptions,
                            onDismissRequest = { showPhotoOptions = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Upload New Photo", fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showPhotoOptions = false
                                    photoPickerLauncher.launch("image/*")
                                }
                            )
                            if (!userData?.profilePhotoUrl.isNullOrEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Remove Photo", color = AccentRed, fontSize = 14.sp) },
                                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = AccentRed, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        showPhotoOptions = false
                                        viewModel.updateProfilePhoto("")
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(12.dp))

                // Badges row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Passenger", fontSize = 13.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                    }

                    val (badgeColor, badgeIcon, badgeText) = when (verificationStatus) {
                        "pending"  -> Triple(Color(0xFFF59E0B), Icons.Default.HourglassEmpty, "Pending")
                        "approved" -> Triple(Color(0xFF10B981), Icons.Default.VerifiedUser,   "Verified")
                        "rejected" -> Triple(Color(0xFFEF4444), Icons.Default.Cancel,          "Rejected")
                        else       -> Triple(AccentOrange,      Icons.Default.Warning,          "Unverified")
                    }
                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(badgeIcon, null, tint = badgeColor, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(badgeText, fontSize = 13.sp, color = badgeColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // ── Account Section ───────────────────────────────────────
        MenuSectionLabel("Account")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                MenuRow(
                    Icons.Default.Person, PrimaryColor, "My Profile", "View your account info",
                    onClick = { showProfileDialog = true }
                )
                RowDivider()
                MenuRow(
                    Icons.Default.Lock, AccentOrange, "Change Password", "Update your password",
                    onClick = { showChangePasswordDialog = true }
                )
                RowDivider()
                // Update Profile Picture row
                MenuRow(
                    Icons.Default.AccountCircle, AccentDarkBlue, "Update Profile Picture", "Change your avatar",
                    onClick = { photoPickerLauncher.launch("image/*") }
                )
                
                if (!userData?.profilePhotoUrl.isNullOrEmpty()) {
                    RowDivider()
                    MenuRow(
                        Icons.Default.DeleteForever, AccentRed, "Remove Current Photo", "Delete your avatar",
                        onClick = { viewModel.updateProfilePhoto("") }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Support Section ───────────────────────────────────────
        MenuSectionLabel("Support")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                MenuRow(
                    Icons.Default.History, PrimaryColor, "Issue History", "View status of reported issues",
                    onClick = { onIssueHistoryClick() }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Verification Section ──────────────────────────────────
        MenuSectionLabel("Verification")
        Spacer(Modifier.height(8.dp))

        VerificationCard(viewModel = viewModel)

        Spacer(Modifier.height(16.dp))

        // ── Mode Section ──────────────────────────────────────────
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
                            when (verificationStatus) {
                                "approved" -> "Tap to switch to Rider Mode"
                                "pending"  -> "Awaiting admin approval"
                                "rejected" -> "Verification rejected"
                                else       -> "Verify yourself first"
                            },
                            fontSize = 12.sp, color = TextSecondary
                        )
                    }
                }
                Switch(
                    checked = false,
                    onCheckedChange = { if (verificationStatus == "approved") onSwitchToRider() },
                    enabled = verificationStatus == "approved",
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryColor,
                        disabledUncheckedThumbColor = Color.Gray,
                        disabledUncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Session ───────────────────────────────────────────────
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

    // ── Profile Dialog ────────────────────────────────────────────
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = CardBackground,
            title = { Text("My Profile", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Show profile photo inside dialog too
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor.copy(alpha = 0.12f))
                                .border(2.dp, PrimaryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!userData?.profilePhotoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = userData!!.profilePhotoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, null, tint = PrimaryColor, modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    ProfileDetailRow("Name", userName)
                    ProfileDetailRow("Email", userEmail)
                    ProfileDetailRow("Role", "Passenger")
                    ProfileDetailRow(
                        "Verified",
                        when (verificationStatus) {
                            "approved" -> "Yes ✓"
                            "pending"  -> "Pending..."
                            "rejected" -> "Rejected"
                            else       -> "No"
                        }
                    )
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

    // ── Change Password Dialog ────────────────────────────────────
    if (showChangePasswordDialog) {
        ChangePasswordDialog(onDismiss = { showChangePasswordDialog = false })
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
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardBackground,
        title = { Text("Change Password", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (error.isNotEmpty()) {
                    Text(error, color = AccentRed, fontSize = 12.sp)
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
                        current.isEmpty() || newPass.isEmpty() || confirm.isEmpty() -> error = "Please fill all fields"
                        newPass != confirm -> error = "Passwords do not match"
                        !isPasswordStrong(newPass) -> error = "Password is too weak"
                        else -> onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("Update", color = Color.White) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}