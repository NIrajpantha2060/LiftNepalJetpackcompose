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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Issue
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.CloudinaryUploader
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.IssueViewModel
import com.example.liftnepal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun IssueSection(
    issueViewModel: IssueViewModel,
    currentUser: User?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var screenshotUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf("") }

    val submitState by issueViewModel.submitIssueState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> screenshotUri = uri }

    // Reset form on success
    LaunchedEffect(submitState) {
        if (submitState is Result.Success) {
            selectedCategory = ""
            title = ""
            description = ""
            screenshotUri = null
            uploadError = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Report an Issue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Let us know what went wrong", fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        // Category chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IssueCategoryCard(Modifier.weight(1f), Icons.Default.DirectionsCar, "Ride",    PrimaryColor,   selectedCategory) { selectedCategory = "Ride" }
            IssueCategoryCard(Modifier.weight(1f), Icons.Default.Payment,       "Payment", AccentDarkBlue, selectedCategory) { selectedCategory = "Payment" }
            IssueCategoryCard(Modifier.weight(1f), Icons.Default.Person,        "Driver",  AccentOrange,   selectedCategory) { selectedCategory = "Driver" }
            IssueCategoryCard(Modifier.weight(1f), Icons.Default.MoreHoriz,     "Other",   TextSecondary,  selectedCategory) { selectedCategory = "Other" }
        }

        Spacer(Modifier.height(20.dp))

        // Form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Issue Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title") },
                    placeholder = { Text("e.g. Driver was late", color = UnselectedNavItem) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Title, null, tint = PrimaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = DividerColor,
                        focusedLabelColor = PrimaryColor,
                        cursorColor = PrimaryColor
                    )
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe your issue") },
                    placeholder = { Text("Tell us what happened...", color = UnselectedNavItem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 6,
                    leadingIcon = { Icon(Icons.Default.Description, null, tint = PrimaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = DividerColor,
                        focusedLabelColor = PrimaryColor,
                        cursorColor = PrimaryColor
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Screenshot upload
                Text("Attach Screenshot (Optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (screenshotUri != null) 180.dp else 100.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceVariant)
                        .border(1.5.dp, DividerColor, RoundedCornerShape(14.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (screenshotUri != null) {
                        AsyncImage(
                            model = screenshotUri,
                            contentDescription = "Screenshot",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(PrimaryColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Change", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, tint = PrimaryColor, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(6.dp))
                            Text("Tap to add screenshot", fontSize = 13.sp, color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (uploadError.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(uploadError, fontSize = 11.sp, color = AccentRed)
                }

                Spacer(Modifier.height(20.dp))

                val isFormValid = title.isNotBlank() && description.isNotBlank() && selectedCategory.isNotEmpty()
                val isLoading = isUploading || submitState is Result.Loading

                Button(
                    onClick = {
                        scope.launch {
                            isUploading = true
                            uploadError = ""
                            var screenshotUrl = ""

                            // Upload screenshot to liftnepal_issues preset ✅
                            if (screenshotUri != null) {
                                val uploadResult = CloudinaryUploader.uploadImage(
                                    context = context,
                                    imageUri = screenshotUri!!,
                                    preset = CloudinaryUploader.PRESET_ISSUES  // ✅ Fixed
                                )
                                when (uploadResult) {
                                    is Result.Success -> screenshotUrl = uploadResult.data
                                    is Result.Error   -> {
                                        uploadError = "Screenshot upload failed: ${uploadResult.message}"
                                        isUploading = false
                                        return@launch
                                    }
                                    else -> {}
                                }
                            }

                            isUploading = false

                            val issue = Issue(
                                userId        = currentUser?.uid ?: "",
                                userName      = currentUser?.displayName ?: "",
                                userPhotoUrl  = currentUser?.profilePhotoUrl ?: "",
                                userPhone     = currentUser?.phoneNumber ?: "",
                                userType      = "user",
                                category      = selectedCategory,
                                title         = title,
                                description   = description,
                                screenshotUrl = screenshotUrl
                            )
                            issueViewModel.submitIssue(issue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Submit Issue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Success banner
        if (submitState is Result.Success) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AccentGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Issue Submitted!", fontWeight = FontWeight.Bold, color = AccentGreen, fontSize = 14.sp)
                        Text("We'll get back to you soon.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            LaunchedEffect(submitState) {
                kotlinx.coroutines.delay(3000)
                issueViewModel.clearSubmitState()
            }
        }

        // Error banner
        if (submitState is Result.Error) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null, tint = AccentRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        (submitState as Result.Error).message,
                        fontSize = 13.sp,
                        color = AccentRed
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun IssueCategoryCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    color: Color,
    selectedCategory: String,
    onClick: () -> Unit
) {
    val isSelected = selectedCategory == label
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f) else CardBackground
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 0.dp else 2.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                fontSize = 10.sp,
                color = if (isSelected) color else TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Keep for backwards compat if used elsewhere
@Composable
fun IssueTypeCard(modifier: Modifier, icon: ImageVector, label: String, color: Color) {
    IssueCategoryCard(modifier, icon, label, color, "") {}
}

@Composable
fun PastIssueCard(title: String, description: String, status: String) {
    val statusColor = when (status) {
        "Resolved"    -> AccentGreen
        "In Progress" -> AccentOrange
        else          -> AccentRed
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}