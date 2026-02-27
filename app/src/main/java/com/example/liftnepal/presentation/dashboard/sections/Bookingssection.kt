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

data class UserBooking(
    val id: String,
    val riderName: String,
    val from: String,
    val to: String,
    val date: String,
    val time: String,
    val price: String,
    val status: String
)

private val myBookings = listOf(
    UserBooking("1", "Rajesh Hamal",   "Thamel, Kathmandu",  "Patan Durbar Square", "2025-06-10", "08:00 AM", "NPR 250", "Completed"),
    UserBooking("2", "Bikash Tamang",  "New Baneshwor",      "Boudhanath Stupa",    "2025-06-11", "10:00 AM", "NPR 180", "Completed"),
    UserBooking("3", "Sita Gurung",    "Kalimati",           "Bhaktapur Durbar",    "2025-06-13", "09:30 AM", "NPR 300", "Upcoming"),
    UserBooking("4", "Dipak Rai",      "Maharajgunj",        "Kirtipur",            "2025-06-14", "11:00 AM", "NPR 220", "Upcoming"),
    UserBooking("5", "Anita Shrestha", "Lazimpat",           "Swayambhunath",       "2025-06-05", "07:30 AM", "NPR 150", "Cancelled"),
)

@Composable
fun BookingsSection() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Upcoming", "Completed", "Cancelled")

    val displayList = myBookings.filter {
        selectedFilter == "All" || it.status == selectedFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .padding(16.dp)
    ) {
        // Header
        Text(text = "My Bookings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Your ride booking history", fontSize = 13.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(14.dp))

        // Summary strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryColor),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MyBookingSummary("5",  "Total")
                MyBookingSummary("2",  "Upcoming")
                MyBookingSummary("2",  "Completed")
                MyBookingSummary("1",  "Cancelled")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryColor,
                        selectedLabelColor = CardBackground,
                        containerColor = CardBackground,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (displayList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EventBusy, null, tint = UnselectedNavItem, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No bookings found", color = TextSecondary, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(displayList) { booking -> MyBookingCard(booking) }
            }
        }
    }
}

@Composable
fun MyBookingSummary(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CardBackground)
        Text(label, fontSize = 11.sp, color = CardBackground.copy(alpha = 0.8f))
    }
}

@Composable
fun MyBookingCard(booking: UserBooking) {
    val statusColor = when (booking.status) {
        "Completed" -> AccentGreen
        "Upcoming"  -> AccentBlue
        "Cancelled" -> AccentRed
        else        -> TextSecondary
    }
    val statusIcon = when (booking.status) {
        "Completed" -> Icons.Default.CheckCircle
        "Upcoming"  -> Icons.Default.Schedule
        "Cancelled" -> Icons.Default.Cancel
        else        -> Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row — rider + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PrimaryColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(booking.riderName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                        Text("Booking #${booking.id}", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(booking.status, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            // From → To
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(booking.from, fontSize = 13.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                Spacer(modifier = Modifier.width(7.dp))
                Box(modifier = Modifier.width(2.dp).height(14.dp).background(DividerColor))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(booking.to, fontSize = 13.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date, time, price chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RideInfoChip(icon = Icons.Default.DateRange, label = booking.date)
                    RideInfoChip(icon = Icons.Default.AccessTime, label = booking.time)
                }
                Text(booking.price, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
        }
    }
}