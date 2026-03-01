package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RideHistorySection(rideViewModel: RideViewModel, authViewModel: AuthViewModel) {
    val riderRidesState by rideViewModel.riderRidesState.collectAsState()
    val userDataState by authViewModel.currentUserData.collectAsState()
    val userData = (userDataState as? Result.Success)?.data

    var selectedRide by remember { mutableStateOf<Ride?>(null) }

    LaunchedEffect(userData) {
        userData?.uid?.let { rideViewModel.fetchRidesByRider(it) }
    }

    val historyRides = (riderRidesState as? Result.Success)?.data?.filter { it.status != "active" && it.status != "booked" } ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().background(RiderBackground).padding(16.dp)) {
        Text("Ride History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RiderTextPrimary)
        Text("Your past ride activities", fontSize = 13.sp, color = RiderTextSecondary)
        Spacer(Modifier.height(16.dp))

        if (historyRides.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = RiderDivider)
                    Text("No history found", color = RiderTextSecondary)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historyRides) { ride -> 
                    RiderHistoryCard(ride = ride, onClick = { selectedRide = ride }) 
                }
            }
        }
    }

    if (selectedRide != null) {
        RideHistoryDetailsDialog(ride = selectedRide!!, onDismiss = { selectedRide = null })
    }
}

@Composable
fun RiderHistoryCard(ride: Ride, onClick: () -> Unit) {
    val isCompleted = ride.status == "completed"
    val statusText = when {
        isCompleted -> "Completed"
        ride.cancelledBy == "passenger" -> "Cancelled by ${ride.passengerName}"
        else -> "You cancelled"
    }
    val statusColor = if (isCompleted) RiderAccentGreen else AccentRed

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = RiderCardBackground), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(Modifier.size(40.dp).background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = statusColor, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${ride.startLocation} → ${ride.destination}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = RiderTextPrimary, maxLines = 1)
                    Text("ID: ${ride.rideId}", fontSize = 11.sp, color = RiderPrimary, fontWeight = FontWeight.Bold)
                    Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(ride.createdAt)), fontSize = 11.sp, color = RiderTextSecondary)
                }
            }
            Box(Modifier.background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun RideHistoryDetailsDialog(ride: Ride, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Ride Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextSecondary) }
                }
                HorizontalDivider()

                DetailRow(Icons.Default.Fingerprint, RiderPrimary, "Ride ID", ride.rideId)

                if (ride.bookedBy.isNotEmpty()) {
                    Text("Passenger Info", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(50.dp).clip(CircleShape).background(RiderPrimary), contentAlignment = Alignment.Center) {
                            if (ride.passengerPhotoUrl.isNotEmpty()) AsyncImage(model = ride.passengerPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                            else Text(ride.passengerName.firstOrNull()?.toString() ?: "P", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(ride.passengerName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(ride.passengerPhone, fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    HorizontalDivider()
                }

                Text("Route Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                DetailRow(Icons.Default.MyLocation, RiderAccentGreen, "From", ride.startLocation)
                DetailRow(Icons.Default.LocationOn, AccentRed, "To", ride.destination)
                DetailRow(Icons.Default.Place, RiderPrimary, "Pickup", ride.pickupLocation.ifEmpty { "Not specified" })
                DetailRow(Icons.Default.AccessTime, RiderPrimary, "Time", ride.rideTime)
                DetailRow(Icons.Default.Payments, RiderPrimary, "Cost", "NPR ${ride.cost}")
                
                val finalStatusText = when {
                    ride.status == "completed" -> "COMPLETED"
                    ride.cancelledBy == "passenger" -> "CANCELLED BY PASSENGER"
                    else -> "CANCELLED BY YOU"
                }
                DetailRow(Icons.Default.Info, if (ride.status == "completed") RiderAccentGreen else AccentRed, "Final Status", finalStatusText)

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary), shape = RoundedCornerShape(14.dp)) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}