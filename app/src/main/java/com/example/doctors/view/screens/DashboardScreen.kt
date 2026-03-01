package com.example.doctors.view.screens

import androidx.compose.foundation.*
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
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.model.Appointment
import com.example.doctors.model.Specialist
import com.example.doctors.model.User
import com.example.doctors.navigation.Routes
import com.example.doctors.viewmodel.BookingViewModel
import com.example.doctors.viewmodel.UserViewModel

// UI theme colors
val SpecialistCardBg = Color(0xFFE9F0FF)

@Composable
fun DashboardScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel
) {
    // State
    val userState = userViewModel.state
    val user = userState.currentUser
    val bookings = bookingViewModel.bookedAppointments

    var selectedSpecialty by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Mock data for specialists
    val specialists = remember {
        listOf(
            Specialist("1", "Dr. Andrew Jamison", "Neurologist", "4.8", "MBBS", "$120", "1500+", "8 Years", "Expert in brain disorders...", "Fellowship in USA", R.drawable.img_doctor_welcome),
            Specialist("2", "Dr. Sarah Konor", "Dentist", "4.9", "DDS", "$100", "1200+", "5 Years", "Specialist in cosmetic...", "Orthodontic Expert", R.drawable.sarahdoctor),
            Specialist("3", "Dr. John Doe", "Psychologist", "4.7", "PhD", "$150", "800+", "10 Years", "Focuses on mental health...", "Clinical Psychologist", R.drawable.johndoctor),
            Specialist("4", "Dr. Mike Tyson", "ENT", "4.5", "MBBS", "$90", "2000+", "12 Years", "Specialist in Ear, Nose...", "Surgeon", R.drawable.mikedoctor)
        )
    }

    // Filter logic
    val filteredSpecialists = remember(selectedSpecialty, searchQuery) {
        specialists.filter { doctor ->
            val matchesSpecialty = if (selectedSpecialty == null) true else doctor.type == selectedSpecialty
            val matchesQuery = doctor.name.contains(searchQuery, ignoreCase = true) ||
                    doctor.type.contains(searchQuery, ignoreCase = true)
            matchesSpecialty && matchesQuery
        }
    }

    // Fetch data
    LaunchedEffect(Unit) {
        if (user == null) { userViewModel.fetchCurrentUser() }
        bookingViewModel.fetchBookings()
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2).copy(alpha = 0.8f), Color.White)
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = { ModernBottomNav(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(paddingValues)
        ) {
            // Profile
            item { TopHeaderSection(user, navController) }

            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search doctor or specialty...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = { if (searchQuery.isNotEmpty()) { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null) } } },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent),
                    singleLine = true
                )
            }

            // Bookings
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upcoming Appointments", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("See All", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, modifier = Modifier.clickable {
                        navController.navigate(Routes.ALL_BOOKINGS)
                    })
                }

                if (bookings.isEmpty()) { EmptyBookingState() }
                else {
                    val latestBooking = bookings.last()
                    UpcomingAppointmentCard(appointment = latestBooking, onCancelClick = { bookingViewModel.cancelBooking(latestBooking) })
                }
            }

            // Specialty
            item {
                SectionTitle("Doctor Specialty")
                SpecialtyRow(selectedSpec = selectedSpecialty, onSpecSelected = { spec ->
                    selectedSpecialty = if (selectedSpecialty == spec) null else spec
                })
            }

            // Specialists
            item {
                SectionTitle(
                    title = "Our Specialist",
                    onSeeAllClick = {
                        navController.navigate("${Routes.ALL_BOOKINGS}?selectedTab=specialist")
                    }
                )
            }

            items(filteredSpecialists) { doctor ->
                MainSpecialistCard(doctor, navController)
            }

            // Results
            if (filteredSpecialists.isEmpty()) {
                item { Text("No results found.", modifier = Modifier.fillMaxWidth().padding(40.dp), textAlign = TextAlign.Center, color = Color.Gray) }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// Specialty filter row
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

// Top user info
@Composable
fun TopHeaderSection(user: User?, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { navController.navigate("profile") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (user != null) "Hi, ${user.firstName}" else "Welcome!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Kathmandu, Nepal",
                    color = Color.White.copy(0.8f),
                    fontSize = 12.sp
                )
            }
        }
        IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
            Icon(Icons.Default.Notifications, null, tint = Color.White)
        }
    }
}

// Single booking card
@Composable
fun UpcomingAppointmentCard(appointment: Appointment, onCancelClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = appointment.imageRes), contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(appointment.doctorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(appointment.doctorType, color = Color.Gray, fontSize = 13.sp)
                }
                IconButton(onClick = onCancelClick) { Icon(Icons.Default.Delete, null, tint = Color.Red.copy(0.7f)) }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("📅 ${appointment.date}", fontSize = 12.sp, color = Color.Gray)
                Text("⏰ ${appointment.time}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// Doctor card
@Composable
fun MainSpecialistCard(doctor: Specialist, navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).clickable { navController.navigate("doctor_detail/${doctor.id}") },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = SpecialistCardBg)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            Image(painter = painterResource(id = doctor.imageRes), contentDescription = null, modifier = Modifier.size(190.dp, 240.dp).align(Alignment.BottomEnd).offset(x = 10.dp), contentScale = ContentScale.Fit)
            Column(modifier = Modifier.padding(24.dp).fillMaxHeight()) {
                Surface(color = Color.White.copy(0.9f), shape = RoundedCornerShape(20.dp)) {
                    Text("Online", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(doctor.type, color = Color.Gray, fontSize = 13.sp)
                Text(doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${doctor.price} /session", fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallGlassyBox(doctor.rating, "Rating", Icons.Default.Star)
                    SmallGlassyBox(doctor.degree, "Degree", Icons.Default.CheckCircle)
                }
            }
        }
    }
}

// Small info card
@Composable
fun SmallGlassyBox(title: String, subtitle: String, icon: ImageVector) {
    Surface(color = Color.White.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp), modifier = Modifier.width(95.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, fontSize = 8.sp, color = Color.Gray)
        }
    }
}

// Section header
@Composable
fun SectionTitle(
    title: String,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (onSeeAllClick != null) {
            Text(
                text = "See All",
                color = Color(0xFF1976D2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
    }
}

// Empty state
@Composable
fun EmptyBookingState() {
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 24.dp).background(Color.White.copy(0.2f), RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("No Upcoming Appointments", color = Color.White)
    }
}