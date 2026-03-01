package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.AuthViewModel
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import com.example.liftnepal.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────
// Shared Admin UI Components
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminSectionHeader(title: String, count: Int, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AdminAccentSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AdminAccent, modifier = Modifier.size(18.dp))
            }
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AdminCard)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("$count total", fontSize = 11.sp, color = AdminTextSecondary)
        }
    }
}

@Composable
fun AdminCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AdminCard)
            .border(0.8.dp, AdminBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────
// USERS SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminUsersSection(viewModel: AuthViewModel) {
    val usersState by viewModel.usersList.collectAsState()
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchAllUsers() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = usersState) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            is Result.Error -> {
                Text(state.message, color = AdminAccent, modifier = Modifier.align(Alignment.Center))
            }
            is Result.Success -> {
                val users = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(AdminBg),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { AdminSectionHeader("All Users", users.size, Icons.Default.People) }
                    items(users) { user ->
                        AdminCardContainer {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedUser = user },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(44.dp).clip(CircleShape).background(AdminAccentSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (user.displayName.ifEmpty { "U" }).first().toString().uppercase(),
                                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                                        )
                                    }
                                    Column {
                                        Text(user.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                                        Text(user.email, fontSize = 11.sp, color = AdminTextSecondary)
                                    }
                                }
                                IconButton(onClick = { userToDelete = user }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AdminAccent, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }

        selectedUser?.let { user ->
            UserDetailDialog(user = user, onDismiss = { selectedUser = null })
        }
        userToDelete?.let { user ->
            DeleteConfirmDialog(
                userName = user.displayName,
                onConfirm = { viewModel.deleteUser(user.uid); userToDelete = null },
                onDismiss = { userToDelete = null }
            )
        }
    }
}

@Composable
fun UserDetailDialog(user: User, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(24.dp),
        title = { Text("User Details", fontWeight = FontWeight.Bold, color = AdminTextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailItem("User ID", user.uid)
                DetailItem("Username", user.displayName)
                DetailItem("Email", user.email)
                DetailItem("Phone", user.phoneNumber)
                DetailItem("Joined", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(user.createdAt)))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = AdminAccent, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = AdminTextMuted, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = AdminTextPrimary)
    }
}

@Composable
fun DeleteConfirmDialog(userName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Delete User", fontWeight = FontWeight.Bold, color = AdminTextPrimary) },
        text = { Text("Are you sure you want to delete $userName? This action cannot be undone.", color = AdminTextSecondary) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Delete", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = AdminTextSecondary) }
        }
    )
}

// ─────────────────────────────────────────────────────────────
// RIDES SECTION (ADMIN)
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminRidesSection(rideViewModel: RideViewModel) {
    val allRidesState by rideViewModel.adminAllRidesState.collectAsState()
    val deleteRideState by rideViewModel.deleteRideState.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedRide by remember { mutableStateOf<Ride?>(null) }
    var rideToDelete by remember { mutableStateOf<Ride?>(null) }

    LaunchedEffect(Unit) { rideViewModel.fetchAllRidesForAdmin() }

    // Refresh after delete
    LaunchedEffect(deleteRideState) {
        if (deleteRideState is Result.Success) {
            rideViewModel.clearDeleteRideState()
        }
    }

    val allRides = (allRidesState as? Result.Success)?.data ?: emptyList()

    val filteredRides = when (selectedFilter) {
        "Active"    -> allRides.filter { it.status == "active" }
        "Booked"    -> allRides.filter { it.status == "booked" }
        "Completed" -> allRides.filter { it.status == "completed" }
        "Cancelled" -> allRides.filter { it.status == "cancelled" }
        else        -> allRides
    }

    // Status color helper
    fun statusColor(status: String) = when (status) {
        "active"    -> Color(0xFF3B82F6)
        "booked"    -> Color(0xFFF59E0B)
        "completed" -> Color(0xFF10B981)
        "cancelled" -> Color(0xFFEF4444)
        else        -> AdminTextMuted
    }

    Box(modifier = Modifier.fillMaxSize().background(AdminBg)) {
        when {
            allRidesState is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            allRidesState is Result.Error -> {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((allRidesState as Result.Error).message, color = AdminAccent)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { rideViewModel.fetchAllRidesForAdmin() }, colors = ButtonDefaults.buttonColors(containerColor = AdminAccent)) { Text("Retry") }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Header
                    item {
                        AdminSectionHeader("All Rides", filteredRides.size, Icons.Default.DirectionsCar)
                    }

                    // Summary stats row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AdminCard)
                                .border(0.8.dp, AdminBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AdminRideStat(allRides.count { it.status == "active" }.toString(),    "Active",    Color(0xFF3B82F6))
                            AdminRideStat(allRides.count { it.status == "booked" }.toString(),    "Booked",    Color(0xFFF59E0B))
                            AdminRideStat(allRides.count { it.status == "completed" }.toString(), "Done",      Color(0xFF10B981))
                            AdminRideStat(allRides.count { it.status == "cancelled" }.toString(), "Cancelled", Color(0xFFEF4444))
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Filter chips
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf("All", "Active", "Booked", "Completed", "Cancelled")) { filter ->
                                val isSelected = filter == selectedFilter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AdminAccent,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    // Empty state
                    if (filteredRides.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.DirectionsCar, null, tint = AdminTextMuted, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("No rides found", color = AdminTextSecondary, fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        items(filteredRides) { ride ->
                            AdminRideCard(
                                ride = ride,
                                statusColor = statusColor(ride.status),
                                onClick = { selectedRide = ride },
                                onDelete = { rideToDelete = ride }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail dialog
    selectedRide?.let { ride ->
        AdminRideDetailDialog(
            ride = ride,
            onDismiss = { selectedRide = null },
            onDelete = { rideToDelete = ride; selectedRide = null }
        )
    }

    // Delete confirm dialog
    rideToDelete?.let { ride ->
        AdminDeleteRideDialog(
            rideInfo = "${ride.startLocation} → ${ride.destination}",
            onConfirm = { rideViewModel.deleteRide(ride.rideId); rideToDelete = null },
            onDismiss = { rideToDelete = null }
        )
    }
}

@Composable
fun AdminRideStat(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = AdminTextSecondary)
    }
}

@Composable
fun AdminRideCard(ride: Ride, statusColor: Color, onClick: () -> Unit, onDelete: () -> Unit) {
    val statusLabel = when (ride.status) {
        "active"    -> "Active"
        "booked"    -> "Booked"
        "completed" -> "Completed"
        "cancelled" -> when (ride.cancelledBy) {
            "rider"     -> "Cancelled by Rider"
            "passenger" -> "Cancelled by Passenger"
            else        -> "Cancelled"
        }
        else -> ride.status
    }

    AdminCardContainer {
        // Top row: rider avatar + name + status badge + delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).clickable { onClick() }
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(AdminAccentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    if (ride.riderPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ride.riderPhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            ride.riderName.firstOrNull()?.toString() ?: "R",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminAccent
                        )
                    }
                }
                Column {
                    Text(ride.riderName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                    Text(ride.riderPhone.ifEmpty { "No phone" }, fontSize = 11.sp, color = AdminTextSecondary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(statusLabel, statusColor)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = AdminBorder)
        Spacer(Modifier.height(10.dp))

        // Route
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.MyLocation, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
            Text(ride.startLocation, fontSize = 12.sp, color = AdminTextPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, null, tint = AdminTextMuted, modifier = Modifier.size(12.dp))
            Icon(Icons.Default.LocationOn, null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
            Text(ride.destination, fontSize = 12.sp, color = AdminTextPrimary, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        // Bottom row: cost + time + passenger if booked
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Payments, null, tint = AdminAccent, modifier = Modifier.size(14.dp))
                    Text("NPR ${ride.cost}", fontSize = 12.sp, color = AdminTextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AccessTime, null, tint = AdminTextMuted, modifier = Modifier.size(14.dp))
                    Text(ride.rideTime, fontSize = 12.sp, color = AdminTextSecondary)
                }
            }
            if (ride.bookedBy.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                    Text(ride.passengerName, fontSize = 11.sp, color = AdminTextSecondary)
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(ride.createdAt)),
            fontSize = 10.sp,
            color = AdminTextMuted
        )
    }
}

@Composable
fun AdminRideDetailDialog(ride: Ride, onDismiss: () -> Unit, onDelete: () -> Unit) {
    val statusLabel = when (ride.status) {
        "active"    -> "ACTIVE"
        "booked"    -> "BOOKED"
        "completed" -> "COMPLETED"
        "cancelled" -> when (ride.cancelledBy) {
            "rider"     -> "CANCELLED BY RIDER"
            "passenger" -> "CANCELLED BY PASSENGER"
            else        -> "CANCELLED"
        }
        else -> ride.status.uppercase()
    }
    val statusColor = when (ride.status) {
        "active"    -> Color(0xFF3B82F6)
        "booked"    -> Color(0xFFF59E0B)
        "completed" -> Color(0xFF10B981)
        "cancelled" -> Color(0xFFEF4444)
        else        -> AdminTextMuted
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(AdminCard)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Ride Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = AdminTextSecondary) }
            }

            // Status banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(statusLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }

            HorizontalDivider(color = AdminBorder)

            // Rider info
            Text("Rider", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(AdminAccentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    if (ride.riderPhotoUrl.isNotEmpty()) {
                        AsyncImage(model = ride.riderPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Text(ride.riderName.firstOrNull()?.toString() ?: "R", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent)
                    }
                }
                Column {
                    Text(ride.riderName, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                    Text(ride.riderPhone.ifEmpty { "No phone" }, fontSize = 12.sp, color = AdminTextSecondary)
                }
            }

            // Vehicle info
            HorizontalDivider(color = AdminBorder)
            Text("Vehicle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
            Text(ride.vehicleNumber, fontSize = 14.sp, color = AdminTextPrimary)
            if (ride.vehiclePhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = ride.vehiclePhotoUrl, contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Passenger info (if any)
            if (ride.bookedBy.isNotEmpty()) {
                HorizontalDivider(color = AdminBorder)
                Text("Passenger", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ride.passengerPhotoUrl.isNotEmpty()) {
                            AsyncImage(model = ride.passengerPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Text(ride.passengerName.firstOrNull()?.toString() ?: "P", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                    }
                    Column {
                        Text(ride.passengerName, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                        Text(ride.passengerPhone, fontSize = 12.sp, color = AdminTextSecondary)
                    }
                }
            }

            // Route details
            HorizontalDivider(color = AdminBorder)
            Text("Route", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextMuted)
            AdminDetailRow(Icons.Default.MyLocation,  Color(0xFF10B981), "From",    ride.startLocation)
            AdminDetailRow(Icons.Default.LocationOn,  Color(0xFFEF4444), "To",      ride.destination)
            AdminDetailRow(Icons.Default.Place,       AdminAccent,       "Pickup",  ride.pickupLocation.ifEmpty { "Not specified" })
            AdminDetailRow(Icons.Default.AccessTime,  AdminAccent,       "Time",    ride.rideTime)
            AdminDetailRow(Icons.Default.Payments,    AdminAccent,       "Cost",    "NPR ${ride.cost}")
            AdminDetailRow(Icons.Default.CalendarToday, AdminTextMuted,  "Created", SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(ride.createdAt)))

            if (ride.remarks.isNotEmpty()) {
                AdminDetailRow(Icons.Default.Notes, AdminTextMuted, "Remarks", ride.remarks)
            }

            // Action buttons
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Close", color = AdminTextSecondary) }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete Ride", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Column {
            Text(label, fontSize = 10.sp, color = AdminTextMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTextPrimary)
        }
    }
}

@Composable
fun AdminDeleteRideDialog(rideInfo: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AdminCard,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Delete Ride", fontWeight = FontWeight.Bold, color = AdminTextPrimary) },
        text = { Text("Delete \"$rideInfo\"? This cannot be undone.", color = AdminTextSecondary) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Delete", color = Color.White, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = AdminTextSecondary) }
        }
    )
}

// ─────────────────────────────────────────────────────────────
// VERIFICATION SECTION (ADMIN)
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminVerificationSection(viewModel: AuthViewModel) {
    val allVerificationsState by viewModel.allVerifications.collectAsState()
    var selectedPair by remember { mutableStateOf<Pair<User, Verification>?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchAllVerifications() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = allVerificationsState) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
            }
            is Result.Error -> {
                Text(state.message, color = AdminAccent, modifier = Modifier.align(Alignment.Center))
            }
            is Result.Success -> {
                val list = state.data
                val pendingCount = list.count { it.second.status == "pending" }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(AdminBg),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { AdminSectionHeader("Verifications", pendingCount, Icons.Default.VerifiedUser) }

                    if (list.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CheckCircle, null, tint = AdminTextMuted, modifier = Modifier.size(48.dp))
                                    Text("No verifications yet", color = AdminTextMuted, fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        items(list) { (user, verification) ->
                            VerificationRequestCard(
                                user = user,
                                verification = verification,
                                onReview = { selectedPair = Pair(user, verification) }
                            )
                        }
                    }
                }
            }
            else -> {}
        }

        selectedPair?.let { (user, verification) ->
            VerificationReviewDialog(
                user = user,
                verification = verification,
                onApprove = { viewModel.approveVerification(verification.uid); selectedPair = null },
                onReject = { viewModel.rejectVerification(verification.uid); selectedPair = null },
                onDismiss = { selectedPair = null }
            )
        }
    }
}

@Composable
fun VerificationRequestCard(user: User, verification: Verification, onReview: () -> Unit) {
    val (statusColor, statusLabel) = when (verification.status) {
        "pending"  -> Color(0xFFF59E0B) to "Pending"
        "approved" -> Color(0xFF10B981) to "Approved"
        "rejected" -> Color(0xFFEF4444) to "Rejected"
        else       -> AdminTextMuted    to "Unknown"
    }

    AdminCardContainer {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(AdminAccentSoft), contentAlignment = Alignment.Center) {
                    Text((user.displayName.ifEmpty { "U" }).first().toString().uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminAccent)
                }
                Column {
                    Text(user.displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminTextPrimary)
                    Text("License: ${verification.licenseNumber}", fontSize = 11.sp, color = AdminTextSecondary)
                    Text("Expiry: ${verification.licenseExpiryDate}", fontSize = 11.sp, color = AdminTextSecondary)
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(statusLabel, statusColor)
                if (verification.status == "pending") {
                    TextButton(onClick = onReview, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Review", color = AdminAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationReviewDialog(user: User, verification: Verification, onApprove: () -> Unit, onReject: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier.fillMaxWidth(0.92f).clip(RoundedCornerShape(24.dp)).background(AdminCard).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Review Verification", fontWeight = FontWeight.Bold, color = AdminTextPrimary, fontSize = 18.sp)

            if (verification.licensePhotoUrl.isNotEmpty()) {
                Text("License Photo", fontSize = 12.sp, color = AdminTextMuted, fontWeight = FontWeight.SemiBold)
                AsyncImage(
                    model = verification.licensePhotoUrl, contentDescription = "License Photo",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(AdminBg),
                    contentScale = ContentScale.Crop
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailItem("Name", user.displayName)
                DetailItem("Email", user.email)
                DetailItem("License Number", verification.licenseNumber)
                DetailItem("Expiry Date", verification.licenseExpiryDate)
                DetailItem("Submitted", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(verification.submittedAt)))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onReject, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Reject", fontWeight = FontWeight.SemiBold)
                }
                Button(onClick = onApprove, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Approve", fontWeight = FontWeight.SemiBold)
                }
            }

            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancel", color = AdminTextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// ISSUES SECTION
// ─────────────────────────────────────────────────────────────

@Composable
fun AdminIssuesSection() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Issues & Support coming soon...", color = AdminTextSecondary)
    }
}