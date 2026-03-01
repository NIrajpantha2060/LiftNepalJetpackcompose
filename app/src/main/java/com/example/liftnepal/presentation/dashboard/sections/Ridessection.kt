package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RidesSection(rideViewModel: RideViewModel, authViewModel: AuthViewModel? = null) {
    val allRidesState by rideViewModel.allRidesState.collectAsState()
    val updateState by rideViewModel.updateRideState.collectAsState()
    val myBookingsState by rideViewModel.myBookingsState.collectAsState()
    
    // Get current user data for booking
    val currentUserDataState by authViewModel?.currentUserData?.collectAsState() ?: remember { mutableStateOf(null) }
    val userData = (currentUserDataState as? Result.Success)?.data

    LaunchedEffect(userData) {
        rideViewModel.fetchAllActiveRides()
        userData?.uid?.let { rideViewModel.fetchMyBookings(it) }
    }
    
    LaunchedEffect(updateState) {
        if (updateState is Result.Success) {
            rideViewModel.fetchAllActiveRides()
            userData?.uid?.let { rideViewModel.fetchMyBookings(it) }
            rideViewModel.clearUpdateRideState()
        }
    }

    var selectedRide by remember { mutableStateOf<Ride?>(null) }

    // Check if user has an active booked ride
    val myActiveBooking = (myBookingsState as? Result.Success)?.data?.find { it.status == "booked" }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceVariant)) {
        if (myActiveBooking != null) {
            // Show ONLY the booked ride
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Your Current Booking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                RideCard(ride = myActiveBooking, onClick = { selectedRide = myActiveBooking })
            }
        } else {
            // Show all active rides
            when (allRidesState) {
                is Result.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryColor) }
                is Result.Success -> {
                    val rides = (allRidesState as Result.Success<List<Ride>>).data
                    if (rides.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                                Text("No rides available", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(rides) { ride -> RideCard(ride = ride, onClick = { selectedRide = ride }) }
                        }
                    }
                }
                is Result.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = { rideViewModel.fetchAllActiveRides() }) { Text("Retry") }
                }
                else -> {}
            }
        }
    }

    if (selectedRide != null) {
        RideDetailsDialog(
            ride = selectedRide!!,
            userData = userData,
            onDismiss = { selectedRide = null },
            onBook = { rideId, user -> rideViewModel.bookRide(rideId, user) },
            onCancelBooking = { rideId, userId -> rideViewModel.cancelBooking(rideId, userId, "passenger", "cancelled") }
        )
    }
}

@Composable
fun RideCard(ride: Ride, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryColor), contentAlignment = Alignment.Center) {
                        if (ride.riderPhotoUrl.isNotEmpty()) AsyncImage(model = ride.riderPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        else Text(ride.riderName.firstOrNull()?.toString() ?: "R", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(ride.riderName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(ride.riderPhone.ifEmpty { "No phone" }, fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = PrimaryColor.copy(alpha = 0.1f)) {
                    Text("NPR ${ride.cost}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                    Text(ride.rideTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                    Text(ride.startLocation, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                    Text(ride.destination, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Text(ride.vehicleNumber, fontSize = 13.sp, color = TextSecondary)
                }
                TextButton(onClick = onClick) { Text("View Details", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun RideDetailsDialog(
    ride: Ride, 
    userData: User?,
    onDismiss: () -> Unit,
    onBook: (String, User) -> Unit,
    onCancelBooking: (String, String) -> Unit
) {
    var showMpinDialog by remember { mutableStateOf(false) }
    var bookingError by remember { mutableStateOf("") }
    
    val isMyRide = userData?.uid == ride.riderId
    val isBookedByMe = userData?.uid == ride.bookedBy

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Ride Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onDismiss) { Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextSecondary) }
                }
                HorizontalDivider(color = DividerColor)
                
                if (bookingError.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().background(AccentRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Text(bookingError, color = AccentRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Rider Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(60.dp).clip(CircleShape).background(PrimaryColor), contentAlignment = Alignment.Center) {
                        if (ride.riderPhotoUrl.isNotEmpty()) AsyncImage(model = ride.riderPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        else Text(ride.riderName.firstOrNull()?.toString() ?: "R", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(ride.riderName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(ride.riderPhone.ifEmpty { "Not available" }, fontSize = 13.sp, color = TextSecondary)
                    }
                }
                HorizontalDivider(color = DividerColor)
                Text("Vehicle Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (ride.vehiclePhotoUrl.isNotEmpty()) AsyncImage(model = ride.vehiclePhotoUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Text(ride.vehicleNumber, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                HorizontalDivider(color = DividerColor)
                Text("Route Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                DetailRow(Icons.Default.AccessTime, PrimaryColor, "Departure", ride.rideTime)
                DetailRow(Icons.Default.MyLocation, AccentGreen, "From", ride.startLocation)
                DetailRow(Icons.Default.LocationOn, AccentRed, "To", ride.destination)
                DetailRow(Icons.Default.Place, PrimaryColor, "Pickup", ride.pickupLocation.ifEmpty { "Not specified" })
                DetailRow(Icons.Default.Payments, PrimaryColor, "Cost", "NPR ${ride.cost}")
                
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Close") }
                    if (isBookedByMe) {
                        Button(onClick = { onCancelBooking(ride.rideId, userData!!.uid); onDismiss() }, Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AccentRed)) { Text("Cancel Booking") }
                    } else {
                        Button(
                            onClick = { 
                                if (isMyRide) {
                                    bookingError = "You cannot book your own ride!"
                                } else if (userData == null) {
                                    bookingError = "Please log in to book"
                                } else {
                                    showMpinDialog = true 
                                }
                            }, 
                            Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) { Text("Book Ride") }
                    }
                }
            }
        }
    }

    if (showMpinDialog) {
        MpinPaymentDialog(
            cost = ride.cost,
            onDismiss = { showMpinDialog = false },
            onConfirm = { 
                showMpinDialog = false
                onBook(ride.rideId, userData!!)
                onDismiss()
            }
        )
    }
}

@Composable
fun MpinPaymentDialog(cost: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var mpin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Booking", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Total Amount: NPR $cost", fontWeight = FontWeight.SemiBold, color = PrimaryColor)
                Text("Please enter your 4-digit MPIN to confirm the booking.", fontSize = 13.sp)
                OutlinedTextField(
                    value = mpin, onValueChange = { if (it.length <= 4) mpin = it; error = "" },
                    label = { Text("MPIN") }, visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                if (error.isNotEmpty()) Text(error, color = AccentRed, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = { if (mpin == "2060") onConfirm() else error = "Incorrect MPIN!" }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) { Text("Pay & Book") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}