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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingsSection(
    rideViewModel: RideViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val myBookingsState by rideViewModel.myBookingsState.collectAsState()
    val userDataState by authViewModel.currentUserData.collectAsState()
    val userData = (userDataState as? Result.Success)?.data

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedRide by remember { mutableStateOf<Ride?>(null) }

    LaunchedEffect(userData) {
        userData?.uid?.let { rideViewModel.fetchMyBookings(it) }
    }

    val historyBookings = (myBookingsState as? Result.Success)?.data?.filter { it.status == "completed" || it.status == "cancelled" } ?: emptyList()
    
    val filteredList = when (selectedFilter) {
        "Completed" -> historyBookings.filter { it.status == "completed" }
        "Cancelled" -> historyBookings.filter { it.status == "cancelled" }
        else        -> historyBookings
    }

    Column(modifier = Modifier.fillMaxSize().background(SurfaceVariant).padding(16.dp)) {
        Text("Booking History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Track your past trip outcomes", fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(14.dp))

        // Summary banner
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = PrimaryColor), elevation = CardDefaults.cardElevation(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceAround) {
                BookingStat(historyBookings.size.toString(), "Total")
                BookingStat(historyBookings.count { it.status == "completed" }.toString(), "Done")
                BookingStat(historyBookings.count { it.status == "cancelled" }.toString(), "No")
            }
        }

        Spacer(Modifier.height(14.dp))

        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Completed", "Cancelled").forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryColor, selectedLabelColor = Color.White)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, tint = UnselectedNavItem, modifier = Modifier.size(48.dp))
                    Text("No history found", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredList) { ride -> 
                    BookingCard(ride = ride, onClick = { selectedRide = ride }) 
                }
            }
        }
    }

    if (selectedRide != null) {
        BookingDetailsDialog(ride = selectedRide!!, onDismiss = { selectedRide = null })
    }
}

@Composable
fun BookingStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun BookingCard(ride: Ride, onClick: () -> Unit) {
    val statusColor = if (ride.status == "completed") AccentGreen else AccentRed
    
    val statusText = when {
        ride.status == "completed" -> "Completed"
        ride.cancelledBy == "rider" -> "Cancelled by Rider"
        else -> "You cancelled"
    }

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(PrimaryColor.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        if (ride.riderPhotoUrl.isNotEmpty()) AsyncImage(model = ride.riderPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        else Icon(Icons.Default.Person, null, tint = PrimaryColor)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(ride.riderName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                        Text("Booking ID: ${ride.rideId}", fontSize = 10.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                        Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(ride.createdAt)), fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Box(modifier = Modifier.background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(12.dp))
            Text("${ride.startLocation} → ${ride.destination}", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("NPR ${ride.cost}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                Text("View Details", fontSize = 12.sp, color = AccentBrightBlue)
            }
        }
    }
}

@Composable
fun BookingDetailsDialog(ride: Ride, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Trip Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextSecondary) }
                }
                HorizontalDivider()
                
                DetailRow(Icons.Default.Fingerprint, PrimaryColor, "Booking ID", ride.rideId)

                Text("Rider Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(50.dp).clip(CircleShape).background(PrimaryColor), contentAlignment = Alignment.Center) {
                        if (ride.riderPhotoUrl.isNotEmpty()) AsyncImage(model = ride.riderPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        else Text(ride.riderName.firstOrNull()?.toString() ?: "R", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(ride.riderName, fontWeight = FontWeight.Bold)
                        Text(ride.riderPhone, fontSize = 13.sp, color = TextSecondary)
                    }
                }
                
                HorizontalDivider()
                Text("Vehicle Info", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Vehicle Number: ${ride.vehicleNumber}", fontSize = 14.sp)
                if (ride.vehiclePhotoUrl.isNotEmpty()) AsyncImage(model = ride.vehiclePhotoUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                
                HorizontalDivider()
                Text("Route Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                DetailRow(Icons.Default.MyLocation, AccentGreen, "From", ride.startLocation)
                DetailRow(Icons.Default.LocationOn, AccentRed, "To", ride.destination)
                DetailRow(Icons.Default.Place, PrimaryColor, "Pickup", ride.pickupLocation)
                DetailRow(Icons.Default.AccessTime, PrimaryColor, "Time", ride.rideTime)
                DetailRow(Icons.Default.Payments, PrimaryColor, "Final Cost", "NPR ${ride.cost}")
                
                val finalStatus = when {
                    ride.status == "completed" -> "COMPLETED"
                    ride.cancelledBy == "rider" -> "CANCELLED BY RIDER"
                    else -> "CANCELLED BY YOU"
                }
                DetailRow(Icons.Default.Info, if (ride.status == "completed") AccentGreen else AccentRed, "Outcome", finalStatus)

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor), shape = RoundedCornerShape(12.dp)) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}