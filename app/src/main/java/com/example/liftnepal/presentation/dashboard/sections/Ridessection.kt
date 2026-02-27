package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*

data class AvailableRide(
    val id: String,
    val riderName: String,
    val from: String,
    val to: String,
    val date: String,
    val time: String,
    val price: String,
    val seats: Int,
    val rating: String
)

private val availableRides = listOf(
    AvailableRide("1", "Rajesh Hamal",   "Thamel, Kathmandu",  "Patan Durbar Square", "2025-06-15", "08:00 AM", "NPR 250", 3, "4.8"),
    AvailableRide("2", "Sita Gurung",    "Kalimati",           "Bhaktapur Durbar",    "2025-06-15", "09:30 AM", "NPR 300", 2, "4.6"),
    AvailableRide("3", "Bikash Tamang",  "New Baneshwor",      "Boudhanath Stupa",    "2025-06-15", "10:00 AM", "NPR 180", 4, "4.9"),
    AvailableRide("4", "Anita Shrestha", "Lazimpat",           "Swayambhunath",       "2025-06-16", "07:30 AM", "NPR 150", 2, "4.7"),
    AvailableRide("5", "Dipak Rai",      "Maharajgunj",        "Kirtipur",            "2025-06-16", "11:00 AM", "NPR 220", 3, "4.5"),
    AvailableRide("6", "Priya Lama",     "Chabahil",           "Balaju",              "2025-06-17", "02:00 PM", "NPR 130", 1, "4.8"),
)

@Composable
fun RidesSection() {
    var showBookDialog by remember { mutableStateOf(false) }
    var selectedRide by remember { mutableStateOf<AvailableRide?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Available Rides",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Find and book a ride near you",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(availableRides) { ride ->
                AvailableRideCard(
                    ride = ride,
                    onBook = {
                        selectedRide = ride
                        showBookDialog = true
                    },
                    onViewDetails = {
                        selectedRide = ride
                        showDetailsDialog = true
                    }
                )
            }
        }
    }

    // Book Ride Dialog
    if (showBookDialog && selectedRide != null) {
        BookRideDialog(
            ride = selectedRide!!,
            onDismiss = { showBookDialog = false },
            onConfirm = { showBookDialog = false }
        )
    }

    // View Details Dialog
    if (showDetailsDialog && selectedRide != null) {
        RideDetailsDialog(
            ride = selectedRide!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
}

@Composable
fun AvailableRideCard(
    ride: AvailableRide,
    onBook: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Rider info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(PrimaryColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = ride.riderName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = AccentOrange, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = ride.rating, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                // Price badge
                Box(
                    modifier = Modifier
                        .background(PrimaryColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = ride.price,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CardBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            // From → To
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = ride.from, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Arrow
            Row {
                Spacer(modifier = Modifier.width(7.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(DividerColor)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = ride.to, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date, time, seats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RideInfoChip(icon = Icons.Default.DateRange, label = ride.date)
                RideInfoChip(icon = Icons.Default.AccessTime, label = ride.time)
                RideInfoChip(icon = Icons.Default.EventSeat, label = "${ride.seats} seats")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor),
                    border = ButtonDefaults.outlinedButtonBorder.copy()
                ) {
                    Text("View Details", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onBook,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Book Ride", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun RideInfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(SurfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun BookRideDialog(ride: AvailableRide, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardBackground,
        title = {
            Text("Confirm Booking", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BookingDetailRow("Rider", ride.riderName)
                BookingDetailRow("From", ride.from)
                BookingDetailRow("To", ride.to)
                BookingDetailRow("Date", ride.date)
                BookingDetailRow("Time", ride.time)
                BookingDetailRow("Price", ride.price)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("Confirm", color = CardBackground) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun RideDetailsDialog(ride: AvailableRide, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardBackground,
        title = {
            Text("Ride Details", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BookingDetailRow("Rider Name", ride.riderName)
                BookingDetailRow("Rating", "⭐ ${ride.rating}")
                BookingDetailRow("From", ride.from)
                BookingDetailRow("To", ride.to)
                BookingDetailRow("Date", ride.date)
                BookingDetailRow("Time", ride.time)
                BookingDetailRow("Price", ride.price)
                BookingDetailRow("Available Seats", "${ride.seats}")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) { Text("Close", color = CardBackground) }
        }
    )
}

@Composable
fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}