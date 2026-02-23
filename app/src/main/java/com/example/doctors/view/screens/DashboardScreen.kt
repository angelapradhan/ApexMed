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

@Composable
fun SpecialtyRow(selectedSpec: String?, onSpecSelected: (String) -> Unit) {
    val specialties = listOf(
        Pair("Neurologist", Icons.Default.Info),
        Pair("Dentist", Icons.Default.Face),
        Pair("Psychologist", Icons.Default.Favorite),
        Pair("ENT", Icons.Default.Call)
    )
    LazyRow(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(specialties) { spec ->
            val isSelected = selectedSpec == spec.first
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(30.dp)).clickable { onSpecSelected(spec.first) },
                color = if (isSelected) Color(0xFF1976D2) else Color.White,
                border = BorderStroke(1.dp, if (isSelected) Color(0xFF1976D2) else Color.LightGray.copy(0.4f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(spec.second, null, tint = if (isSelected) Color.White else Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(spec.first, color = if (isSelected) Color.White else Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- REST OF THE COMPONENTS (No changes here, just for completeness) ---

@Composable
fun TopHeaderSection(user: com.example.doctors.model.User?, navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user?.profileImageUrl ?: R.drawable.profile,
                contentDescription = "Profile",
                modifier = Modifier.size(55.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.profile),
                error = painterResource(R.drawable.profile)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = if (user != null) "Hi, ${user.firstName}" else "Welcome Back!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                    Text("Dubai, Marina, UAE", color = Color.White.copy(0.8f), fontSize = 12.sp)
                }
            }
        }
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(Color.White.copy(0.2f), CircleShape)
                .clip(CircleShape)
                .clickable { navController.navigate(Routes.NOTIFICATIONS) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
            // Sano indicator dot
            Box(
                modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd).offset(x = (-2).dp, y = 2.dp)
            )
        }
    }
}

// --- Dynamic Appointment Card ---
@Composable
fun UpcomingAppointmentCard(appointment: com.example.doctors.model.Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Doctor ko image dynamic
                Image(
                    painter = painterResource(id = appointment.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(appointment.doctorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(appointment.doctorType, color = Color.Gray, fontSize = 13.sp)
                }
                Text("View", color = LoginBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = LoginBlue, modifier = Modifier.size(16.dp))
                    Text(" ${appointment.date}", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, null, tint = LoginBlue, modifier = Modifier.size(16.dp))
                    Text(" ${appointment.time}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoginBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join Now", color = Color.White)
            }
        }
    }
}

// --- Empty State UI ---
@Composable
fun EmptyBookingState() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.4f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Text("No Upcoming Appointments", color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}
@Composable
fun MainSpecialistCard(doctor: Specialist, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable { navController.navigate("doctor_detail/${doctor.id}") },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = SpecialistCardBg)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) { // Card ko height fix gareko

            // Image Section (Sabai ko lagi eutai size)
            Image(
                painter = painterResource(id = doctor.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(190.dp, 240.dp) // Yaha size fix garepachi sabai doctor eutai dekhinchhan
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp),
                contentScale = ContentScale.Fit
            )

            // Content Section
            Column(modifier = Modifier.padding(24.dp).fillMaxHeight()) {
                // Online Badge
                Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(20.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF4CAF50), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Online", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(doctor.type, color = Color.Gray, fontSize = 13.sp)
                Text(doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("${doctor.price} /session", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black)

                Spacer(modifier = Modifier.weight(1f))

                // Glassy Info Boxes
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallGlassyBox(doctor.rating, "Rating", Icons.Default.Star)
                    SmallGlassyBox(doctor.degree, "Degree", Icons.Default.CheckCircle)
                }
            }
        }
    }
}

