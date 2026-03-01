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
fun RiderIssueSection(
    issueViewModel: IssueViewModel,
    currentUser: User?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var issueDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var screenshotUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf("") }

    val submitState by issueViewModel.submitIssueState.collectAsState()

    val categories = listOf("Technical Problem", "Passenger Issue", "Payment Issue", "App Bug", "Other")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> screenshotUri = uri }

    // Reset form on success
    LaunchedEffect(submitState) {
        if (submitState is Result.Success) {
            selectedCategory = ""
            issueDescription = ""
            screenshotUri = null
            uploadError = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiderBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderSecondary),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReportProblem, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Report an Issue", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("We'll look into it as soon as possible", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Category selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Issue Category")
                Spacer(Modifier.height(12.dp))
                categories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RiderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = RiderSurface,
                                    labelColor = RiderTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = RiderDivider,
                                    selectedBorderColor = RiderPrimary
                                )
                            )
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Image upload card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Attach Photo (Optional)")
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (screenshotUri != null) 180.dp else 120.dp)
                        .background(RiderSurface, RoundedCornerShape(14.dp))
                        .border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (screenshotUri != null) {
                        AsyncImage(
                            model = screenshotUri,
                            contentDescription = "Issue Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Change overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(RiderPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Change", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                null,
                                tint = RiderPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Upload Issue Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Tap to select from gallery", fontSize = 11.sp, color = RiderTextSecondary)
                        }
                    }
                }

                if (uploadError.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(uploadError, fontSize = 11.sp, color = Color(0xFFEF4444))
                }
            }
        }

        // Description card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Issue Description")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = issueDescription,
                    onValueChange = { if (it.length <= 500) issueDescription = it },
                    placeholder = { Text("Describe the issue in detail...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${issueDescription.length}/500",
                    fontSize = 11.sp,
                    color = RiderTextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Validation hint
        if (selectedCategory.isEmpty() && issueDescription.isNotBlank()) {
            Text(
                "Please select a category",
                fontSize = 11.sp,
                color = Color(0xFFEF4444),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        val isFormValid = issueDescription.isNotBlank() && selectedCategory.isNotEmpty()
        val isLoading = isUploading || submitState is Result.Loading

        // Submit button
        Button(
            onClick = {
                scope.launch {
                    isUploading = true
                    uploadError = ""
                    var screenshotUrl = ""

                    // Upload photo to liftnepal_issues preset
                    if (screenshotUri != null) {
                        val uploadResult = CloudinaryUploader.uploadImage(
                            context = context,
                            imageUri = screenshotUri!!,
                            preset = CloudinaryUploader.PRESET_ISSUES  // ✅
                        )
                        when (uploadResult) {
                            is Result.Success -> screenshotUrl = uploadResult.data
                            is Result.Error   -> {
                                uploadError = "Photo upload failed: ${uploadResult.message}"
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
                        userType      = "rider",           // ✅ rider type
                        category      = selectedCategory,
                        title         = selectedCategory,  // use category as title for rider
                        description   = issueDescription,
                        screenshotUrl = screenshotUrl
                    )
                    issueViewModel.submitIssue(issue)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Submit Issue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Success banner
        if (submitState is Result.Success) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Issue Submitted!", fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 14.sp)
                        Text("Admin will review and get back to you.", fontSize = 12.sp, color = RiderTextSecondary)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        (submitState as Result.Error).message,
                        fontSize = 13.sp,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}