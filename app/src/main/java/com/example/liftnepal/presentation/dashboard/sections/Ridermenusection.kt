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
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Vehicle
import com.example.liftnepal.data.utils.CloudinaryUploader
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RiderMenuSection(
    userName: String = "John Doe",
    userEmail: String = "john@liftnepal.com",
    onSwitchToUser: () -> Unit = {},
    onLogout: () -> Unit = {},
    onIssueHistoryClick: () -> Unit = {}, // ✅ Added
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val currentUserDataState by authViewModel.currentUserData.collectAsState()
    val userData = (currentUserDataState as? Result.Success)?.data
    
    var showVehicleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiderBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(RiderPrimary.copy(alpha = 0.12f))
                        .border(3.dp, RiderPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userData?.profilePhotoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = userData!!.profilePhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = RiderPrimary, modifier = Modifier.size(46.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, fontSize = 13.sp, color = RiderTextSecondary)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(RiderPrimary.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TwoWheeler, null, tint = RiderPrimary, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(5.dp))
                            Text("Rider", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(RiderOnlineBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(RiderOnlineGreen, CircleShape))
                            Spacer(Modifier.width(5.dp))
                            Text("Online", fontSize = 13.sp, color = RiderOnlineGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // Vehicle section
        RiderSectionLabel("My Vehicle")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (userData?.vehicle != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (userData.vehicle!!.vehiclePhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = userData.vehicle!!.vehiclePhotoUrl,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(60.dp).background(RiderSurface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.DirectionsCar, null, tint = RiderPrimary) }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(userData.vehicle!!.vehicleNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RiderTextPrimary)
                            Text("Saved Vehicle Info", fontSize = 12.sp, color = RiderTextSecondary)
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { showVehicleDialog = true }) {
                            Icon(Icons.Default.Edit, null, tint = RiderPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showVehicleDialog = true }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = RiderPrimary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add Vehicle Info", color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Support section
        RiderSectionLabel("Support")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                RiderMenuRow(
                    icon = Icons.Default.History,
                    iconColor = RiderSecondary,
                    label = "Issue History",
                    subtitle = "View your submitted issues",
                    onClick = { onIssueHistoryClick() }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Mode section
        RiderSectionLabel("Mode")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchToUser() }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(AccentGreen.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SwitchAccount, null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Switch to User Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = RiderTextPrimary)
                    Text("Go back to passenger mode", fontSize = 12.sp, color = RiderTextSecondary)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowRight, null, tint = RiderUnselectedNav, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Session
        RiderSectionLabel("Session")
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.07f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
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

    // Vehicle Dialog
    if (showVehicleDialog) {
        VehicleUpdateDialog(
            currentVehicle = userData?.vehicle,
            onDismiss = { showVehicleDialog = false },
            onUpdate = { vehicle ->
                authViewModel.updateVehicleInfo(vehicle)
                showVehicleDialog = false
            }
        )
    }
}

@Composable
fun VehicleUpdateDialog(
    currentVehicle: Vehicle?,
    onDismiss: () -> Unit,
    onUpdate: (Vehicle) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var vehicleNumber by remember { mutableStateOf(currentVehicle?.vehicleNumber ?: "") }
    var vehiclePhotoUri by remember { mutableStateOf<Uri?>(if (!currentVehicle?.vehiclePhotoUrl.isNullOrEmpty()) Uri.parse(currentVehicle!!.vehiclePhotoUrl) else null) }
    var isUploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        vehiclePhotoUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = RiderCardBackground,
        title = { Text("Update Vehicle Info", fontWeight = FontWeight.Bold, color = RiderTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (error.isNotEmpty()) Text(error, color = AccentRed, fontSize = 12.sp)
                
                OutlinedTextField(
                    value = vehicleNumber, onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Number") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RiderPrimary, unfocusedBorderColor = RiderDivider)
                )
                
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp).background(RiderSurface, RoundedCornerShape(14.dp))
                        .border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp)).clickable { photoPicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (vehiclePhotoUri != null) {
                        AsyncImage(model = vehiclePhotoUri, contentDescription = null, modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, null, tint = RiderPrimary, modifier = Modifier.size(32.dp))
                            Text("Upload Vehicle Photo", fontSize = 12.sp, color = RiderPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        if (vehicleNumber.isEmpty() || vehiclePhotoUri == null) {
                            error = "Fill all fields"
                            return@launch
                        }
                        
                        isUploading = true
                        val finalUrl = if (vehiclePhotoUri.toString().startsWith("http")) {
                            vehiclePhotoUri.toString()
                        } else {
                            val result = CloudinaryUploader.uploadImage(context, vehiclePhotoUri!!, CloudinaryUploader.PRESET_RIDES)
                            if (result is Result.Success) result.data else {
                                error = "Upload failed"
                                isUploading = false
                                return@launch
                            }
                        }
                        isUploading = false
                        onUpdate(Vehicle(vehicleNumber, finalUrl))
                    }
                },
                enabled = !isUploading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary)
            ) {
                if (isUploading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Save Changes")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = RiderTextSecondary) } }
    )
}

@Composable
fun RiderSectionLabel(text: String) {
    Text(text = text, fontSize = 12.sp, color = RiderTextSecondary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 4.dp))
}

@Composable
fun RiderMenuRow(icon: ImageVector, iconColor: Color, label: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = RiderTextPrimary)
                Text(subtitle, fontSize = 12.sp, color = RiderTextSecondary)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = RiderUnselectedNav, modifier = Modifier.size(20.dp))
    }
}