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
    var vehicleNumber    by remember { mutableStateOf("") }
    var startLocation    by remember { mutableStateOf("") }
    var destination      by remember { mutableStateOf("") }
    var pickupLocation   by remember { mutableStateOf("") }
    var rideTime         by remember { mutableStateOf("") }
    var remarks          by remember { mutableStateOf("") }
    var cost             by remember { mutableStateOf("") }

    // Image states
    var vehicleImageUri  by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var uploadError      by remember { mutableStateOf<String?>(null) }

    // Observe states
    val addRideState by rideViewModel.addRideState.collectAsState()
    val riderRidesState by rideViewModel.riderRidesState.collectAsState()
    val updateRideState by rideViewModel.updateRideState.collectAsState()

    // Get current user data
    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data

    // Fetch rider's rides on load or when user data changes
    LaunchedEffect(userData) {
        userData?.uid?.let {
            rideViewModel.fetchRidesByRider(it)
        }
    }

    // Logic to find if there's an active ride
    val activeRide = (riderRidesState as? Result.Success)?.data?.find { it.status == "active" }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vehicleImageUri = uri
        uploadError = null
    }

    // Handle add ride success
    LaunchedEffect(addRideState) {
        if (addRideState is Result.Success) {
            vehicleNumber = ""
            startLocation = ""
            destination = ""
            pickupLocation = ""
            rideTime = ""
            remarks = ""
            cost = ""
            vehicleImageUri = null
            rideViewModel.clearAddRideState()
            userData?.uid?.let { rideViewModel.fetchRidesByRider(it) }
        }
    }

    // Handle update success (cancellation/completion)
    LaunchedEffect(updateRideState) {
        if (updateRideState is Result.Success) {
            userData?.uid?.let { rideViewModel.fetchRidesByRider(it) }
            rideViewModel.clearUpdateRideState()
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
        if (activeRide != null) {
            ActiveRideCard(activeRide, rideViewModel)
        } else {
            AddRideForm(
                vehicleNumber = vehicleNumber, onVehicleNumberChange = { vehicleNumber = it },
                startLocation = startLocation, onStartLocationChange = { startLocation = it },
                destination = destination, onDestinationChange = { destination = it },
                pickupLocation = pickupLocation, onPickupLocationChange = { pickupLocation = it },
                rideTime = rideTime, onRideTimeChange = { rideTime = it },
                remarks = remarks, onRemarksChange = { remarks = it },
                cost = cost, onCostChange = { cost = it },
                vehicleImageUri = vehicleImageUri, onImageClick = { imagePickerLauncher.launch("image/*") },
                isUploadingImage = isUploadingImage, uploadError = uploadError,
                addRideState = addRideState,
                onAddRide = {
                    scope.launch {
                        if (vehicleNumber.isBlank() || startLocation.isBlank() ||
                            destination.isBlank() || cost.isBlank() || pickupLocation.isBlank() || rideTime.isBlank()) {
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

                        isUploadingImage = true
                        uploadError = null

                        val uploadResult = CloudinaryUploader.uploadImage(
                            context = context, imageUri = vehicleImageUri!!, preset = CloudinaryUploader.PRESET_RIDES
                        )

                        isUploadingImage = false

                        if (uploadResult is Result.Success) {
                            val ride = Ride(
                                riderId = userData.uid, riderName = userData.displayName,
                                riderPhone = userData.phoneNumber, riderPhotoUrl = userData.profilePhotoUrl,
                                vehicleNumber = vehicleNumber, vehiclePhotoUrl = uploadResult.data,
                                startLocation = startLocation, destination = destination,
                                pickupLocation = pickupLocation, rideTime = rideTime,
                                remarks = remarks, cost = cost, status = "active"
                            )
                            rideViewModel.addRide(ride)
                        } else if (uploadResult is Result.Error) {
                            uploadError = uploadResult.message
                        }
                    }
                }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ActiveRideCard(ride: Ride, viewModel: RideViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ride in Progress", fontSize = 14.sp, color = RiderPrimary, fontWeight = FontWeight.Bold)
                    Text("Departure: ${ride.rideTime}", fontSize = 13.sp, color = RiderTextSecondary, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier.background(RiderOnlineBg, RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(RiderOnlineGreen, RoundedCornerShape(50)))
                        Spacer(Modifier.width(6.dp))
                        Text("Active", fontSize = 12.sp, color = RiderOnlineGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = RiderDivider, thickness = 0.8.dp)
            Spacer(Modifier.height(18.dp))

            // Route Info
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen, modifier = Modifier.size(18.dp))
                    Box(modifier = Modifier.width(2.dp).height(30.dp).background(RiderDivider))
                    Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("From", fontSize = 11.sp, color = RiderTextSecondary)
                        Text(ride.startLocation, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                    }
                    Column {
                        Text("To", fontSize = 11.sp, color = RiderTextSecondary)
                        Text(ride.destination, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActiveRideStatChip(Icons.Default.AccessTime, ride.rideTime, Modifier.weight(1f))
                ActiveRideStatChip(Icons.Default.Place, ride.pickupLocation, Modifier.weight(1.5f))
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.updateRideStatus(ride.rideId, "cancelled") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Cancel Ride", color = AccentRed, fontWeight = FontWeight.Bold) }
                
                Button(
                    onClick = { viewModel.updateRideStatus(ride.rideId, "completed") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RiderAccentGreen),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Complete", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun ActiveRideStatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier) {
    Row(
        modifier = modifier.background(RiderSurface, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = RiderPrimary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = RiderTextPrimary, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
fun AddRideForm(
    vehicleNumber: String, onVehicleNumberChange: (String) -> Unit,
    startLocation: String, onStartLocationChange: (String) -> Unit,
    destination: String, onDestinationChange: (String) -> Unit,
    pickupLocation: String, onPickupLocationChange: (String) -> Unit,
    rideTime: String, onRideTimeChange: (String) -> Unit,
    remarks: String, onRemarksChange: (String) -> Unit,
    cost: String, onCostChange: (String) -> Unit,
    vehicleImageUri: Uri?, onImageClick: () -> Unit,
    isUploadingImage: Boolean, uploadError: String?,
    addRideState: Result<Boolean>?, onAddRide: () -> Unit
) {
    // Header card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RiderPrimary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Add New Ride", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text("Let passengers know where you're going", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RiderSectionLabel("Vehicle Info")
            OutlinedTextField(
                value = vehicleNumber, onValueChange = onVehicleNumberChange,
                label = { Text("Vehicle Number") }, placeholder = { Text("e.g. BA 1 PA 1234") },
                leadingIcon = { Icon(Icons.Default.DirectionsCar, null, tint = RiderPrimary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(if (vehicleImageUri != null) 200.dp else 110.dp)
                    .background(RiderSurface, RoundedCornerShape(14.dp)).border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp))
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (vehicleImageUri != null) {
                    AsyncImage(model = vehicleImageUri, contentDescription = null, modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, tint = RiderPrimary, modifier = Modifier.size(28.dp))
                        Text("Upload Vehicle Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (uploadError != null) Text(text = uploadError, color = AccentRed, fontSize = 12.sp)

            RiderSectionLabel("Route & Time")
            OutlinedTextField(
                value = startLocation, onValueChange = onStartLocationChange,
                label = { Text("Starting Location") }, placeholder = { Text("e.g. Thamel, Kathmandu") },
                leadingIcon = { Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )
            OutlinedTextField(
                value = destination, onValueChange = onDestinationChange,
                label = { Text("Destination") }, placeholder = { Text("e.g. New Baneshwor, Kathmandu") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = AccentRed) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )
            OutlinedTextField(
                value = pickupLocation, onValueChange = onPickupLocationChange,
                label = { Text("Pickup Point") }, placeholder = { Text("e.g. Thamel Bus Stop") },
                leadingIcon = { Icon(Icons.Default.Place, null, tint = RiderPrimary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )
            OutlinedTextField(
                value = rideTime, onValueChange = onRideTimeChange,
                label = { Text("Departure Time") }, placeholder = { Text("e.g. 08:00 PM") },
                leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = RiderPrimary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )

            RiderSectionLabel("Pricing & Remarks")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = cost, onValueChange = onCostChange,
                    label = { Text("Cost (NPR)") }, placeholder = { Text("500") },
                    leadingIcon = { Icon(Icons.Default.Money, null, tint = RiderPrimary) },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
                )
            }
            OutlinedTextField(
                value = remarks, onValueChange = onRemarksChange,
                label = { Text("Remarks") }, placeholder = { Text("e.g. AC available...") },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
            )
        }
    }

    if (addRideState is Result.Error) Text((addRideState as Result.Error).message, color = AccentRed, fontSize = 13.sp)

    Button(
        onClick = onAddRide,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary),
        enabled = addRideState !is Result.Loading && !isUploadingImage
    ) {
        if (addRideState is Result.Loading || isUploadingImage) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
        } else {
            Icon(Icons.Default.Add, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Add Ride", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RiderSectionLabel(text: String) {
    Text(text = text, fontSize = 12.sp, color = RiderTextSecondary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 2.dp))
}