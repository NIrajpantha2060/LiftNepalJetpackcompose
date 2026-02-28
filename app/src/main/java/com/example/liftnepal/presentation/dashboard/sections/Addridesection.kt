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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.utils.CloudinaryUploader
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AddRideSection(
    authViewModel: AuthViewModel,
    rideViewModel: RideViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form fields
    var vehicleNumber   by remember { mutableStateOf("") }
    var startLocation   by remember { mutableStateOf("") }
    var destination     by remember { mutableStateOf("") }
    var remarks         by remember { mutableStateOf("") }
    var cost            by remember { mutableStateOf("") }

    // Image states
    var vehicleImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    // Ride submission state
    val addRideState by rideViewModel.addRideState.collectAsState()

    // Get current user data
    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vehicleImageUri = uri
        uploadError = null
    }

    // Handle add ride success
    LaunchedEffect(addRideState) {
        when (addRideState) {
            is Result.Success -> {
                // Clear form
                vehicleNumber = ""
                startLocation = ""
                destination = ""
                remarks = ""
                cost = ""
                vehicleImageUri = null
                rideViewModel.clearAddRideState()
            }
            is Result.Error -> {
                // Error is shown in UI
            }
            else -> {}
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
            colors = CardDefaults.cardColors(containerColor = RiderPrimary),
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
                    Icon(Icons.Default.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Add New Ride", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Fill in the ride details below", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                RiderSectionLabel("Vehicle Details")

                // Vehicle Number
                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Number") },
                    placeholder = { Text("e.g. BA 1 PA 1234") },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, null, tint = RiderPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                // Vehicle Photo Upload
                RiderSectionLabel("Vehicle Photo")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (vehicleImageUri != null) 200.dp else 120.dp)
                        .background(RiderSurface, RoundedCornerShape(14.dp))
                        .border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (vehicleImageUri != null) {
                        AsyncImage(
                            model = vehicleImageUri,
                            contentDescription = "Vehicle Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                null,
                                tint = RiderPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Upload Vehicle Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Tap to select from gallery", fontSize = 11.sp, color = RiderTextSecondary)
                        }
                    }
                }

                // Upload error
                if (uploadError != null) {
                    Text(
                        text = uploadError ?: "",
                        color = AccentRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                RiderSectionLabel("Route Details")

                // Starting Location
                OutlinedTextField(
                    value = startLocation,
                    onValueChange = { startLocation = it },
                    label = { Text("Starting Location") },
                    placeholder = { Text("e.g. Thamel, Kathmandu") },
                    leadingIcon = {
                        Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                // Destination
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g. New Baneshwor, Kathmandu") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, null, tint = AccentRed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                RiderSectionLabel("Additional Info")

                // Cost
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Cost (NPR)") },
                    placeholder = { Text("e.g. 500") },
                    leadingIcon = {
                        Icon(Icons.Default.Money, null, tint = RiderPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                // Remarks
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Description") },
                    placeholder = { Text("e.g. AC available, 2 seats, no smoking...") },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, null, tint = RiderTextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )
            }
        }

        // Error message
        if (addRideState is Result.Error) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, null, tint = AccentRed)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        (addRideState as Result.Error).message,
                        color = AccentRed,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Add Ride Button
        Button(
            onClick = {
                scope.launch {
                    if (vehicleNumber.isBlank() || startLocation.isBlank() ||
                        destination.isBlank() || cost.isBlank()) {
                        uploadError = "Please fill all required fields"
                        return@launch
                    }

                    if (vehicleImageUri == null) {
                        uploadError = "Please upload a vehicle photo"
                        return@launch
                    }

                    if (userData == null) {
                        uploadError = "User data not loaded"
                        return@launch
                    }

                    // Upload vehicle image to Cloudinary
                    isUploadingImage = true
                    uploadError = null

                    val uploadResult = CloudinaryUploader.uploadImage(
                        context = context,
                        imageUri = vehicleImageUri!!,
                        preset = CloudinaryUploader.PRESET_RIDES
                    )

                    isUploadingImage = false

                    when (uploadResult) {
                        is Result.Success -> {
                            val vehiclePhotoUrl = uploadResult.data

                            // Create ride object
                            val ride = Ride(
                                riderId = userData.uid,
                                riderName = userData.displayName,
                                riderPhone = userData.phoneNumber,
                                riderPhotoUrl = userData.profilePhotoUrl,
                                vehicleNumber = vehicleNumber,
                                vehiclePhotoUrl = vehiclePhotoUrl,
                                startLocation = startLocation,
                                destination = destination,
                                remarks = remarks,
                                cost = cost,
                                status = "active"
                            )

                            // Submit to Firebase
                            rideViewModel.addRide(ride)
                        }
                        is Result.Error -> {
                            uploadError = uploadResult.message
                        }
                        else -> {}
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
            enabled = addRideState !is Result.Loading && !isUploadingImage
        ) {
            if (addRideState is Result.Loading || isUploadingImage) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Ride", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Success message
        if (addRideState is Result.Success) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RiderAccentGreen.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = RiderAccentGreen)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Ride added successfully!",
                        color = RiderAccentGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun RiderSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = RiderTextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}