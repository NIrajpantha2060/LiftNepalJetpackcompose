package com.example.liftnepal.presentation.dashboard.sections



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*

@Composable
fun RideHistorySection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiderBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Current Ride ──────────────────────────────────────
        RiderSectionLabel("Current Ride")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(RiderSurface, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TwoWheeler, null, tint = RiderPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("BA 1 PA 1234", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RiderTextPrimary)
                            Text("Active Ride", fontSize = 12.sp, color = RiderTextSecondary)
                        }
                    }
                    // Active badge
                    Box(
                        modifier = Modifier
                            .background(RiderOnlineBg, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(RiderOnlineGreen, RoundedCornerShape(50))
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("Active", fontSize = 12.sp, color = RiderOnlineGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = RiderDivider, thickness = 0.8.dp)
                Spacer(Modifier.height(14.dp))

                // Route info
                Row(verticalAlignment = Alignment.Top) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 3.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen, modifier = Modifier.size(16.dp))
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(24.dp)
                                .background(RiderDivider)
                        )
                        Icon(Icons.Default.LocationOn, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Column {
                            Text("From", fontSize = 11.sp, color = RiderTextSecondary)
                            Text("Thamel, Kathmandu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RiderTextPrimary)
                        }
                        Column {
                            Text("To", fontSize = 11.sp, color = RiderTextSecondary)
                            Text("New Baneshwor, Kathmandu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RiderTextPrimary)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = RiderDivider, thickness = 0.8.dp)
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RideStatChip(Icons.Default.People, "2 Passengers")
                    RideStatChip(Icons.Default.AccessTime, "Started 10 min ago")
                }
            }
        }

        // ── Past Rides ────────────────────────────────────────
        RiderSectionLabel("Ride History")

        val dummyRides = listOf(
            Triple("Thamel → Bhaktapur", "Feb 24, 2026", "Completed"),
            Triple("Lalitpur → Thamel", "Feb 22, 2026", "Completed"),
            Triple("New Road → Airport", "Feb 19, 2026", "Cancelled"),
            Triple("Baneshwor → Patan", "Feb 15, 2026", "Completed"),
        )

        dummyRides.forEach { (route, date, status) ->
            PastRideCard(route = route, date = date, status = status)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun RideStatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(RiderSurface, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, tint = RiderPrimary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 12.sp, color = RiderTextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PastRideCard(route: String, date: String, status: String) {
    val isCompleted = status == "Completed"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isCompleted) RiderSurface else AccentRed.copy(alpha = 0.08f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        null,
                        tint = if (isCompleted) RiderAccentGreen else AccentRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(route, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = RiderTextPrimary)
                    Text(date, fontSize = 12.sp, color = RiderTextSecondary)
                }
            }
            Box(
                modifier = Modifier
                    .background(
                        if (isCompleted) RiderOnlineBg else AccentRed.copy(alpha = 0.08f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    status,
                    fontSize = 11.sp,
                    color = if (isCompleted) RiderOnlineGreen else AccentRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}