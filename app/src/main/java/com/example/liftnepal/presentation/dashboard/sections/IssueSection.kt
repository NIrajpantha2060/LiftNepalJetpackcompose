package com.example.liftnepal.presentation.dashboard.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftnepal.ui.theme.*


@Composable
fun IssueSection() {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Report an Issue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Let us know what went wrong", fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        // Category chips
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IssueTypeCard(Modifier.weight(1f), Icons.Default.DirectionsCar, "Ride",    PrimaryColor)
            IssueTypeCard(Modifier.weight(1f), Icons.Default.Payment,       "Payment", AccentDarkBlue)
            IssueTypeCard(Modifier.weight(1f), Icons.Default.Person,        "Driver",  AccentOrange)
            IssueTypeCard(Modifier.weight(1f), Icons.Default.MoreHoriz,     "Other",   TextSecondary)
        }

        Spacer(Modifier.height(20.dp))

        // Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Issue Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title") },
                    placeholder = { Text("e.g. Driver was late", color = UnselectedNavItem) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Title, null, tint = PrimaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = DividerColor,
                        focusedLabelColor = PrimaryColor,
                        cursorColor = PrimaryColor
                    )
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe your issue") },
                    placeholder = { Text("Tell us what happened...", color = UnselectedNavItem) },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 6,
                    leadingIcon = { Icon(Icons.Default.Description, null, tint = PrimaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = DividerColor,
                        focusedLabelColor = PrimaryColor,
                        cursorColor = PrimaryColor
                    )
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            submitted = true
                            title = ""
                            description = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Issue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Success banner
        if (submitted) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).background(AccentGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Issue Submitted!", fontWeight = FontWeight.Bold, color = AccentGreen, fontSize = 14.sp)
                        Text("We'll get back to you soon.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            LaunchedEffect(submitted) {
                kotlinx.coroutines.delay(3000)
                submitted = false
            }
        }

        Spacer(Modifier.height(20.dp))

        // Past issues
        Text("Your Past Issues", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(10.dp))

        listOf(
            Triple("Driver took wrong route",  "The driver deviated from the suggested route.", "Resolved"),
            Triple("Payment deducted twice",   "Was charged twice for the same ride.",           "In Progress"),
        ).forEach { (t, d, s) ->
            PastIssueCard(t, d, s)
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun IssueTypeCard(modifier: Modifier, icon: ImageVector, label: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PastIssueCard(title: String, description: String, status: String) {
    val statusColor = when (status) {
        "Resolved"    -> AccentGreen
        "In Progress" -> AccentOrange
        else          -> AccentRed
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}