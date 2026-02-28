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
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RidesSection(rideViewModel: RideViewModel) {
    val allRidesState by rideViewModel.allRidesState.collectAsState()

    LaunchedEffect(Unit) {
        rideViewModel.fetchAllActiveRides()
    }

    var selectedRide by remember { mutableStateOf<Ride?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
    ) {
        when (allRidesState) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }

            is Result.Success -> {
                val rides = (allRidesState as Result.Success<List<Ride>>).data

                if (rides.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                            Text("No rides available", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Check back later for new rides", fontSize = 14.sp, color = TextSecondary)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(rides) { ride ->
                            RideCard(ride = ride, onClick = { selectedRide = ride })
                        }
                    }
                }
            }

            is Result.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Error, null, tint = AccentRed, modifier = Modifier.size(48.dp))
                        Text((allRidesState as Result.Error).message, fontSize = 14.sp, color = TextSecondary)
                        Button(
                            onClick = { rideViewModel.fetchAllActiveRides() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) { Text("Retry") }
                    }
                }
            }

            else -> {}
        }
    }

    if (selectedRide != null) {
        RideDetailsDialog(
            ride = selectedRide!!,
            onDismiss = { selectedRide = null }
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rider info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ride.riderPhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ride.riderPhotoUrl,
                                contentDescription = "Rider Photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                ride.riderName.firstOrNull()?.toString() ?: "R",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                    }

                    Column {
                        Text(ride.riderName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(
                            ride.riderPhone.ifEmpty { "No phone" },
                            fontSize = 12.sp,
                            color = if (ride.riderPhone.isEmpty()) TextSecondary.copy(alpha = 0.5f) else TextSecondary
                        )
                    }
                }

                Surface(shape = RoundedCornerShape(8.dp), color = PrimaryColor.copy(alpha = 0.1f)) {
                    Text(
                        "NPR ${ride.cost}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryColor
                    )
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)

            // Route Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Time & Location Summary
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AccessTime, null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        Text(ride.rideTime.ifEmpty { "--:--" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.MyLocation, null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                        Text(ride.startLocation, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                        Text(ride.destination, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                    }
                }
            }

            // Vehicle info + View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Text(ride.vehicleNumber, fontSize = 13.sp, color = TextSecondary)
                }
                TextButton(onClick = onClick) {
                    Text("View Details", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun RideDetailsDialog(ride: Ride, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ride Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = DividerColor, thickness = 0.8.dp)

                // Rider Info
                Text("Rider Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape).background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ride.riderPhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ride.riderPhotoUrl,
                                contentDescription = "Rider Photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                ride.riderName.firstOrNull()?.toString() ?: "R",
                                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                    }

                    Column {
                        Text(ride.riderName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Phone, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Text(
                                ride.riderPhone.ifEmpty { "Not available" },
                                fontSize = 13.sp,
                                color = if (ride.riderPhone.isEmpty()) TextSecondary.copy(alpha = 0.5f) else TextSecondary
                            )
                        }
                    }
                }

                HorizontalDivider(color = DividerColor, thickness = 0.8.dp)

                // Vehicle Info
                Text("Vehicle Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                if (ride.vehiclePhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ride.vehiclePhotoUrl,
                        contentDescription = "Vehicle Photo",
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                    Text(ride.vehicleNumber, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }

                HorizontalDivider(color = DividerColor, thickness = 0.8.dp)

                // Route Details
                Text("Route Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                DetailRow(icon = Icons.Default.AccessTime, iconTint = PrimaryColor, label = "Departure Time", value = ride.rideTime.ifEmpty { "Not specified" })
                DetailRow(icon = Icons.Default.MyLocation, iconTint = AccentGreen, label = "From", value = ride.startLocation)
                DetailRow(icon = Icons.Default.LocationOn, iconTint = AccentRed, label = "To", value = ride.destination)
                DetailRow(icon = Icons.Default.Place, iconTint = PrimaryColor, label = "Pickup Point", value = ride.pickupLocation.ifEmpty { "Not specified" })
                DetailRow(icon = Icons.Default.Money, iconTint = PrimaryColor, label = "Cost", value = "NPR ${ride.cost}")

                // Remarks
                if (ride.remarks.isNotEmpty()) {
                    HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
                    Text("Additional Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(ride.remarks, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
                }

                Text(
                    "Posted ${formatTimestamp(ride.createdAt)}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Close") }

                    Button(
                        onClick = { /* TODO: Book ride */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) { Text("Book Ride") }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        diff < 604800000 -> "${diff / 86400000} days ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}