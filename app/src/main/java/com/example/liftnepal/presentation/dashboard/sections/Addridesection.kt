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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.Vehicle
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

    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data

    var vehicleNumber    by remember { mutableStateOf("") }
    var startLocation    by remember { mutableStateOf("") }
    var destination      by remember { mutableStateOf("") }
    var pickupLocation   by remember { mutableStateOf("") }
    var rideTime         by remember { mutableStateOf("") }
    var remarks          by remember { mutableStateOf("") }
    var cost             by remember { mutableStateOf("") }
    var saveVehicleInfo  by remember { mutableStateOf(false) }

    var vehicleImageUri  by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var uploadError      by remember { mutableStateOf<String?>(null) }

    val addRideState by rideViewModel.addRideState.collectAsState()
    val riderRidesState by rideViewModel.riderRidesState.collectAsState()
    val updateRideState by rideViewModel.updateRideState.collectAsState()

    LaunchedEffect(userData) {
        userData?.vehicle?.let { savedVehicle ->
            if (vehicleNumber.isEmpty()) vehicleNumber = savedVehicle.vehicleNumber
            if (vehicleImageUri == null && savedVehicle.vehiclePhotoUrl.isNotEmpty()) {
                vehicleImageUri = Uri.parse(savedVehicle.vehiclePhotoUrl)
            }
        }
    }

    LaunchedEffect(userData) {
        userData?.uid?.let { rideViewModel.fetchRidesByRider(it) }
    }

    val activeRide = (riderRidesState as? Result.Success)?.data?.find { it.status == "active" || it.status == "booked" }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        vehicleImageUri = uri
        uploadError = null
    }

    LaunchedEffect(addRideState) {
        if (addRideState is Result.Success) {
            startLocation = ""; destination = ""; pickupLocation = ""; rideTime = ""; remarks = ""; cost = ""
            rideViewModel.clearAddRideState()
            userData?.uid?.let { rideViewModel.fetchRidesByRider(it) }
        }
    }

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
                saveVehicleInfo = saveVehicleInfo, onSaveVehicleInfoChange = { saveVehicleInfo = it },
                showSaveCheckbox = userData?.vehicle == null,
                vehicleImageUri = vehicleImageUri, onImageClick = { imagePickerLauncher.launch("image/*") },
                isUploadingImage = isUploadingImage, uploadError = uploadError,
                addRideState = addRideState,
                onAddRide = {
                    scope.launch {
                        if (vehicleNumber.isBlank() || startLocation.isBlank() ||
                            destination.isBlank() || cost.isBlank() || pickupLocation.isBlank() || rideTime.isBlank()) {
                            uploadError = "Please fill all required fields"; return@launch
                        }
                        if (vehicleImageUri == null) { uploadError = "Please upload a vehicle photo"; return@launch }
                        if (userData == null) { uploadError = "User data not loaded"; return@launch }

                        isUploadingImage = true; uploadError = null
                        val vehiclePhotoUrl = if (vehicleImageUri.toString().startsWith("http")) vehicleImageUri.toString()
                        else {
                            val res = CloudinaryUploader.uploadImage(context, vehicleImageUri!!, CloudinaryUploader.PRESET_RIDES)
                            if (res is Result.Success) res.data else { uploadError = (res as? Result.Error)?.message ?: "Upload failed"; isUploadingImage = false; return@launch }
                        }
                        isUploadingImage = false

                        if (userData.vehicle == null && saveVehicleInfo) authViewModel.updateVehicleInfo(Vehicle(vehicleNumber, vehiclePhotoUrl))

                        rideViewModel.addRide(Ride(
                            riderId = userData.uid, riderName = userData.displayName, riderPhone = userData.phoneNumber, riderPhotoUrl = userData.profilePhotoUrl,
                            vehicleNumber = vehicleNumber, vehiclePhotoUrl = vehiclePhotoUrl, startLocation = startLocation, destination = destination,
                            pickupLocation = pickupLocation, rideTime = rideTime, remarks = remarks, cost = cost, status = "active"
                        ))
                    }
                }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ActiveRideCard(ride: Ride, viewModel: RideViewModel) {
    val isBooked = ride.status == "booked"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(if (isBooked) "Ride Booked!" else "Ride in Progress", fontSize = 14.sp, color = if (isBooked) RiderAccentGreen else RiderPrimary, fontWeight = FontWeight.Bold)
                    Text("Departure: ${ride.rideTime}", fontSize = 13.sp, color = RiderTextSecondary)
                }
                Box(modifier = Modifier.background(if (isBooked) RiderOnlineBg else RiderPrimary.copy(alpha = 0.1f), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(if (isBooked) "Booked" else "Active", fontSize = 12.sp, color = if (isBooked) RiderOnlineGreen else RiderPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = RiderDivider)
            Spacer(Modifier.height(18.dp))

            if (isBooked) {
                Text("Passenger Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RiderSurface, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(RiderPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ride.passengerPhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ride.passengerPhotoUrl,
                                contentDescription = "Passenger Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(ride.passengerName.firstOrNull()?.toString() ?: "P", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column {
                        Text(ride.passengerName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = RiderTextPrimary)
                        Text(ride.passengerPhone, fontSize = 13.sp, color = RiderTextSecondary)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* TODO: Call passenger */ }) { Icon(Icons.Default.Phone, null, tint = RiderAccentGreen) }
                }
                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = RiderDivider)
                Spacer(Modifier.height(18.dp))
            }

            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen, modifier = Modifier.size(18.dp))
                    Box(modifier = Modifier.width(2.dp).height(30.dp).background(RiderDivider))
                    Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column { Text("From", fontSize = 11.sp, color = RiderTextSecondary); Text(ride.startLocation, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary) }
                    Column { Text("To", fontSize = 11.sp, color = RiderTextSecondary); Text(ride.destination, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary) }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    // ✅ FIX: Pass "rider" as cancelledBy so passenger sees "Cancelled by Rider" in booking history
                    onClick = { viewModel.updateRideStatus(ride.rideId, "cancelled", "rider") },
                    Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel Ride", color = AccentRed, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.updateRideStatus(ride.rideId, "completed") },
                    Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RiderAccentGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (isBooked) "Finish Ride" else "Complete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
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
    saveVehicleInfo: Boolean, onSaveVehicleInfoChange: (Boolean) -> Unit,
    showSaveCheckbox: Boolean,
    vehicleImageUri: Uri?, onImageClick: () -> Unit,
    isUploadingImage: Boolean, uploadError: String?,
    addRideState: Result<Boolean>?, onAddRide: () -> Unit
) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = RiderPrimary)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
            Spacer(Modifier.width(14.dp))
            Column { Text("Add New Ride", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White); Text("Let passengers know where you're going", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f)) }
        }
    }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = RiderCardBackground)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Vehicle Info", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RiderTextSecondary)
            OutlinedTextField(value = vehicleNumber, onValueChange = onVehicleNumberChange, label = { Text("Vehicle Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (vehicleImageUri != null) 200.dp else 110.dp)
                    .background(RiderSurface, RoundedCornerShape(14.dp))
                    .border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp))
                    .clickable { onImageClick() },
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
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = RiderPrimary, modifier = Modifier.size(28.dp)); Text("Upload Vehicle Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (showSaveCheckbox) { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSaveVehicleInfoChange(!saveVehicleInfo) }) { Checkbox(checked = saveVehicleInfo, onCheckedChange = onSaveVehicleInfoChange, colors = CheckboxDefaults.colors(checkedColor = RiderPrimary)); Text("Save vehicle info for future rides", fontSize = 13.sp, color = RiderTextSecondary) } }
            if (uploadError != null) Text(uploadError, color = AccentRed, fontSize = 12.sp)
            Text("Route & Time", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RiderTextSecondary)
            OutlinedTextField(value = startLocation, onValueChange = onStartLocationChange, label = { Text("Starting Location") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
            OutlinedTextField(value = destination, onValueChange = onDestinationChange, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
            OutlinedTextField(value = pickupLocation, onValueChange = onPickupLocationChange, label = { Text("Pickup Point") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
            OutlinedTextField(value = rideTime, onValueChange = onRideTimeChange, label = { Text("Departure Time") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = DividerColor))
            Text("Pricing & Remarks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RiderTextSecondary)
            OutlinedTextField(value = cost, onValueChange = onCostChange, label = { Text("Cost (NPR)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
            OutlinedTextField(value = remarks, onValueChange = onRemarksChange, label = { Text("Remarks") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider))
        }
    }
    if (addRideState is Result.Error) {
        val errorMsg = (addRideState as Result.Error).message
        Text(errorMsg, color = AccentRed, fontSize = 13.sp)
    }
    Button(onClick = onAddRide, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary), enabled = addRideState !is Result.Loading && !isUploadingImage) {
        if (addRideState is Result.Loading || isUploadingImage) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
        else { Icon(Icons.Default.Add, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("Add Ride", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
    }
}