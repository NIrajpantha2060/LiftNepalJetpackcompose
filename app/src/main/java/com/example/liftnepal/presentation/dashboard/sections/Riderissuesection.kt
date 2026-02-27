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
fun RiderIssueSection() {
    var issueDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }

    val categories = listOf("Technical Problem", "Passenger Issue", "Payment Issue", "App Bug", "Other")

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
            colors = CardDefaults.cardColors(containerColor = RiderSecondary),
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
                    Icon(Icons.Default.ReportProblem, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Report an Issue", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("We'll look into it as soon as possible", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Category selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Issue Category")
                Spacer(Modifier.height(12.dp))
                categories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RiderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = RiderSurface,
                                    labelColor = RiderTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = RiderDivider,
                                    selectedBorderColor = RiderPrimary
                                )
                            )
                        }
                        // fill empty slot if odd
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Image upload card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Attach Photo (Optional)")
                Spacer(Modifier.height(12.dp))
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
                            Icons.Default.AddAPhoto,
                            null,
                            tint = RiderPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Upload Issue Photo", fontSize = 13.sp, color = RiderPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Tap to select from gallery", fontSize = 11.sp, color = RiderTextSecondary)
                    }
                }
            }
        }

        // Description card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RiderCardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                RiderSectionLabel("Issue Description")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = issueDescription,
                    onValueChange = { issueDescription = it },
                    placeholder = { Text("Describe the issue in detail...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RiderPrimary,
                        unfocusedBorderColor = RiderDivider,
                        cursorColor = RiderPrimary
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${issueDescription.length}/500",
                    fontSize = 11.sp,
                    color = RiderTextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Submit button
        Button(
            onClick = { /* UI only */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RiderPrimary)
        ) {
            Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Submit Issue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(8.dp))
    }
}