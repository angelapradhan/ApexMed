package com.example.doctors.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.viewmodel.AuthViewModel

// --- COLORS & STYLES ---
val LoginBlue = Color(0xFF1976D2)
val SpecialistCardBg = Color(0xFFE9F0FF)

// --- DATA MODEL ---
data class Specialist(
    val id: String,
    val name: String,
    val type: String,
    val rating: String,
    val degree: String,
    val price: String,
    val patientCount: String,
    val experience: String,
    val about: String,
    val subtitle: String,
    val imageRes: Int
)

val specialists = listOf(
    Specialist("1", "Dr. Andrew Jamison", "Neurologist", "4.8", "MBBS", "$120", "1500+", "8 Years", "Expert in brain disorders and neurological surgeries.", "Fellowship in USA", R.drawable.img_doctor_welcome),
    Specialist("2", "Dr. Sarah Konor", "Dentist", "4.9", "DDS", "$100", "1200+", "5 Years", "Specialist in cosmetic dentistry and dental implants.", "Orthodontic Expert", R.drawable.sarahdoctor),
    Specialist("3", "Dr. John Doe", "Psychologist", "4.7", "PhD", "$150", "800+", "10 Years", "Focuses on mental health and behavioral therapy.", "Clinical Psychologist", R.drawable.johndoctor),
    Specialist("4", "Dr. Mike Tyson", "ENT", "4.5", "MBBS", "$90", "2000+", "12 Years", "Specialist in Ear, Nose, and Throat surgeries.", "Surgeon", R.drawable.mikedoctor)
)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.state
    val user = authState.currentUser
    var selectedSpecialty by remember { mutableStateOf<String?>(null) }

    val filteredSpecialists = remember(selectedSpecialty) {
        if (selectedSpecialty == null) specialists else specialists.filter { it.type == selectedSpecialty }
    }

    LaunchedEffect(Unit) {
        // 1. User ko data fetch garne (yadi login chha bhane)
        if (user == null) {
            authViewModel.fetchCurrentUser()
        }
        // 2. Firebase bata bookings liyera aune (Real Database sync)
        authViewModel.fetchBookings()
    }

    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF1976D2).copy(alpha = 0.8f), Color.White),
            startY = 0f, endY = 1000f
        )
    }

    val bookings = authViewModel.bookedAppointments

    Scaffold(
        containerColor = Color.White,
        bottomBar = { ModernBottomNav(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(gradientBrush).padding(paddingValues)
        ) {
            item { TopHeaderSection(user, navController) }
//            item {
//                Text(
//                    "Upcoming Appointments",
//                    color = Color.White,
//                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
//                    fontWeight = FontWeight.Bold
//                )
//
//                if (bookings.isEmpty()) {
//                    // Yadi booking chhaina bhane empty state
//                    EmptyBookingState()
//                } else {
//                    // Sabai bhanda latest booking dekhaune
//                    val latestBooking = bookings.last()
//                    UpcomingAppointmentCard(latestBooking)
//                }
//            }
            // DashboardScreen vitra ko Upcoming Appointments section ma yo change garnu:
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Upcoming Appointments",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "See All",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.ALL_BOOKINGS) // Naya Screen ma jane
                        }
                    )
                }

                if (bookings.isEmpty()) {
                    EmptyBookingState()
                } else {
                    // Dashboard ma latest euta matra card
                    UpcomingAppointmentCard(bookings.last())
                }
            }
            item {
                SectionTitle(title = "Doctor Specialty")
                SpecialtyRow(selectedSpec = selectedSpecialty, onSpecSelected = { spec ->
                    selectedSpecialty = if (selectedSpecialty == spec) null else spec
                })
            }

            item { SectionTitle(title = "Our Specialist") }

            items(filteredSpecialists) { doctor ->
                MainSpecialistCard(doctor, navController)
            }

            if (filteredSpecialists.isEmpty()) {
                item {
                    Text("No doctors found.", modifier = Modifier.fillMaxWidth().padding(40.dp), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

