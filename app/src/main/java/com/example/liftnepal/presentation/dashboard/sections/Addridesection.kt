package com.example.liftnepal.presentation.dashboard.sections


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*

@Composable
fun AddRideSection() {
    var vehicleNumber   by remember { mutableStateOf("") }
    var startLocation   by remember { mutableStateOf("") }
    var destination     by remember { mutableStateOf("") }
    var remarks         by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RiderBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderPrimary),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TwoWheeler, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Add New Ride", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Fill in the ride details below", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                RiderSectionLabel("Vehicle Details")

                // Vehicle Number
                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Number") },
                    placeholder = { Text("e.g. BA 1 PA 1234") },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, null, tint = RiderPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                // Vehicle Photo Upload Placeholder
                RiderSectionLabel("Vehicle Photo")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(RiderSurface, RoundedCornerShape(14.dp))
                        .border(1.5.dp, RiderDivider, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            null,
                            tint = RiderPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Upload Vehicle Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Tap to select from gallery", fontSize = 11.sp, color = RiderTextSecondary)
                    }
                }

                RiderSectionLabel("Route Details")

                // Starting Location
                OutlinedTextField(
                    value = startLocation,
                    onValueChange = { startLocation = it },
                    label = { Text("Starting Location") },
                    placeholder = { Text("e.g. Thamel, Kathmandu") },
                    leadingIcon = {
                        Icon(Icons.Default.MyLocation, null, tint = RiderAccentGreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                // Destination
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g. New Baneshwor, Kathmandu") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, null, tint = AccentRed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )

                RiderSectionLabel("Additional Info")

                // Remarks
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Description") },
                    placeholder = { Text("e.g. AC available, 2 seats, no smoking...") },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, null, tint = RiderTextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary,
                        focusedLabelColor = RiderPrimary
                    )
                )
            }
        }

        // Add Ride Button
        Button(
            onClick = { /* UI only */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary)
        ) {
            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add Ride", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun RiderSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = RiderTextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}