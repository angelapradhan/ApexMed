package com.example.doctors.view.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.doctors.model.Appointment
import com.example.doctors.model.allSpecialistsList
import com.example.doctors.viewmodel.BookingViewModel
import com.example.doctors.viewmodel.UserViewModel

@Composable
fun DoctorDetailScreen(
    navController: NavHostController,
    doctorId: String?,
    bookingViewModel: BookingViewModel,
    userViewModel: UserViewModel
) {
    // Data setup
    val doctor = remember(doctorId) {
        allSpecialistsList.find { it.id == doctorId } ?: allSpecialistsList[0]
    }
    val currentUser = userViewModel.state.currentUser
    val patientName = "${currentUser?.firstName ?: ""} ${currentUser?.lastName ?: ""}".trim().ifEmpty { "User" }

    // State
    var selectedDate by remember { mutableStateOf<Int?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedReason by remember { mutableStateOf("Follow Up") }
    var showDialog by remember { mutableStateOf(false) }

    val canBook = selectedDate != null && selectedTime != null && selectedType != null
    val primaryBlue = Color(0xFF1976D2)
    val scrollState = rememberScrollState()

    // Main UI Layout
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color(0xFFE9F0FF))) {
        val screenHeight = maxHeight

        // Doctor Image
        Image(
            painter = painterResource(id = doctor.imageRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.85f)
                .height(450.dp)
                .offset(x = 40.dp, y = 30.dp),
            contentScale = ContentScale.Fit
        )

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp).zIndex(10f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(0.6f), CircleShape)) {
                Icon(Icons.Default.ArrowBack, null)
            }
            IconButton(onClick = { },
                modifier = Modifier.background(Color.White.copy(0.6f), CircleShape)) {
                Icon(Icons.Default.Share, null)
            }
        }

        // Content Scroll
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.height(110.dp))

            // Name & Type
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(doctor.name.replace(" ", "\n"), fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 44.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Surface(color = Color.White.copy(0.8f), shape = RoundedCornerShape(20.dp)) {
                    Text(doctor.type, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(180.dp))

            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth().heightIn(min = screenHeight - 100.dp),
                shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Stats
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        DetailStatItem(doctor.experience, "Experience", Icons.Default.DateRange)
                        DetailStatItem(doctor.patientCount, "Patients", Icons.Default.Person)
                        DetailStatItem(doctor.rating, "Rating", Icons.Default.Star)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Time Picker
                    AppointmentTimePicker(
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        selectedType = selectedType,
                        onDateSelected = { selectedDate = it },
                        onTimeSelected = { selectedTime = it },
                        onTypeSelected = { selectedType = it }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // About
                    Text("About", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(doctor.about, color = Color.Gray, fontSize = 15.sp, lineHeight = 22.sp)

                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }

        // Bottom Button
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color.White, shadowElevation = 15.dp
        ) {
            Button(
                onClick = { if (canBook) showDialog = true },
                enabled = canBook,
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canBook) primaryBlue else Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Book Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Confirm Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White,
                title = { Text("Confirm Booking", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Patient: $patientName", fontWeight = FontWeight.Bold)
                        Text("Doctor: ${doctor.name}")
                        Text("Schedule: $selectedDate Jan at $selectedTime")
                        Text("Mode: $selectedType")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newApp = Appointment(
                                doctorName = doctor.name,
                                doctorType = doctor.type,
                                imageRes = doctor.imageRes,
                                date = "$selectedDate Jan",
                                time = selectedTime!!,
                                reason = selectedReason,
                                type = selectedType!!
                            )
                            bookingViewModel.addBooking(newApp)
                            showDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel", color = Color.Red) }
                }
            )
        }
    }
}

// --- COMPONENTS ---

// Date, Time, Type Selector
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppointmentTimePicker(
    selectedDate: Int?,
    selectedTime: String?,
    selectedType: String?,
    onDateSelected: (Int) -> Unit,
    onTimeSelected: (String) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FE), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Date Selection
        Text("Select Date", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val dates = listOf(16 to "MON", 17 to "TUE", 18 to "WED", 19 to "THU", 20 to "FRI", 21 to "SAT", 22 to "SUN")
            dates.forEach { (date, day) ->
                val isSelected = selectedDate == date
                Surface(
                    onClick = { onDateSelected(date) },
                    shape = RoundedCornerShape(25.dp),
                    color = if (isSelected) Color(0xFF8BC34A) else Color.White,
                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEBEBEB)),
                    modifier = Modifier.width(55.dp).height(85.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(date.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Gray)
                        Text(day, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time Selection
        Text("Select Time", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        val timeSlots = listOf("08:00", "09:40", "10:00", "11:50", "12:30", "17:40")
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            timeSlots.forEach { time ->
                val isSelected = selectedTime == time
                Surface(
                    onClick = { onTimeSelected(time) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF7B61FF) else Color.White,
                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEBEBEB)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(time, fontSize = 13.sp, color = if (isSelected) Color.White else Color(0xFF7B61FF))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Type Selection
        Text("Appointment Type", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Online", "In-Person").forEach { type ->
                val isSelected = selectedType == type
                Surface(
                    onClick = { onTypeSelected(type) },
                    modifier = Modifier.weight(1f).height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFFF3F1FF) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF7B61FF) else Color(0xFFEBEBEB))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(type, fontSize = 14.sp, color = if (isSelected) Color(0xFF7B61FF) else Color.Gray)
                    }
                }
            }
        }
    }
}

// Stats Item
@Composable
fun DetailStatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}