package com.example.liftnepal.presentation.dashboard.sections



import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.CloudinaryUploader
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// Main entry — reads from verifications/ table via myVerification
// ─────────────────────────────────────────────────────────────

@Composable
fun VerificationCard(viewModel: AuthViewModel) {
    val myVerificationState by viewModel.myVerification.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyVerification()
    }

    when (val state = myVerificationState) {
        is Result.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(24.dp))
            }
        }
        is Result.Success -> {
            val verification = state.data
            when (verification?.status) {
                "pending"  -> VerificationStatusCard(
                    icon = Icons.Default.HourglassEmpty,
                    title = "Verification Pending",
                    message = "Your documents are under review. Admin will verify soon.",
                    color = Color(0xFFF59E0B)
                )
                "approved" -> VerificationStatusCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "Verified ✓",
                    message = "You are verified! You can now access Rider mode.",
                    color = Color(0xFF10B981)
                )
                "rejected" -> VerifyYourselfCard(viewModel = viewModel, resubmit = true)
                else       -> VerifyYourselfCard(viewModel = viewModel, resubmit = false)
            }
        }
        else -> {
            // null state = not yet loaded, show the submit card
            VerifyYourselfCard(viewModel = viewModel, resubmit = false)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Status display card (pending / approved)
// ─────────────────────────────────────────────────────────────

@Composable
fun VerificationStatusCard(
    icon: ImageVector,
    title: String,
    message: String,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
        }
        Text(message, color = TextSecondary, fontSize = 13.sp)
    }
}

// ─────────────────────────────────────────────────────────────
// Submit / Resubmit card
// ─────────────────────────────────────────────────────────────

@Composable
fun VerifyYourselfCard(viewModel: AuthViewModel, resubmit: Boolean) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(
                0.8.dp,
                if (resubmit) Color(0xFFEF4444).copy(alpha = 0.4f) else PrimaryColor.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (resubmit) Color(0xFFEF4444).copy(alpha = 0.12f)
                        else PrimaryColor.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (resubmit) Icons.Default.Warning else Icons.Default.Badge,
                    contentDescription = null,
                    tint = if (resubmit) Color(0xFFEF4444) else PrimaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    if (resubmit) "Verification Rejected" else "Verify Yourself",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Text(
                    if (resubmit) "Your documents were rejected. Please resubmit."
                    else "Submit your driving license to become a rider",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (resubmit) Color(0xFFEF4444) else PrimaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                if (resubmit) "Resubmit Documents" else "Submit Documents",
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (showDialog) {
        VerificationDialog(
            viewModel = viewModel,
            onDismiss = { showDialog = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Verification form dialog
// ─────────────────────────────────────────────────────────────

@Composable
fun VerificationDialog(
    viewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var licenseNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val submitState by viewModel.verificationSubmitState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    LaunchedEffect(submitState) {
        if (submitState is Result.Success) {
            viewModel.clearVerificationSubmitState()
            onDismiss()
        } else if (submitState is Result.Error) {
            errorMessage = (submitState as Result.Error).message
            isUploading = false
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        containerColor = CardBackground,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "License Verification",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // License Number
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("License Number") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                // Expiry Date
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (MM/DD/YYYY)") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor
                    )
                )

                // License Photo picker
                Text(
                    "License Photo",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .border(
                            1.dp,
                            if (selectedImageUri != null) PrimaryColor else DividerColor,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "License Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text("Tap to select photo", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        licenseNumber.isBlank()  -> errorMessage = "Please enter license number"
                        expiryDate.isBlank()     -> errorMessage = "Please enter expiry date"
                        selectedImageUri == null -> errorMessage = "Please select license photo"
                        else -> {
                            isUploading = true
                            errorMessage = ""
                            scope.launch {
                                when (val uploadResult = CloudinaryUploader.uploadImage(context, selectedImageUri!!)) {
                                    is Result.Success -> {
                                        viewModel.submitVerification(
                                            licenseNumber = licenseNumber,
                                            licenseExpiryDate = expiryDate,
                                            licensePhotoUrl = uploadResult.data
                                        )
                                    }
                                    is Result.Error -> {
                                        errorMessage = "Photo upload failed: ${uploadResult.message}"
                                        isUploading = false
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                },
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Uploading...")
                } else {
                    Text("Submit", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isUploading) onDismiss() }) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}