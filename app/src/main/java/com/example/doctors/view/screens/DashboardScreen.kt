package com.example.doctors.view.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.viewmodel.AuthViewModel

// dummy data
data class Medication(val name: String, val time: String, val taken: Boolean)
data class Doctor(val name: String, val specialization: String, val time: String)
data class Specialization(val name: String, val iconResId: Int)

val medicationList = listOf(
    Medication("Paracetamol", "Today, 08:00", true),
    Medication("Amoxicillin", "Today, 12:00", false),
)

val availableDoctors = listOf(
    Doctor("Dr. Sarah Sulistyo", "General", "08:00 - 15:00"),
    Doctor("Dr. Jane Doe", "Cardiologist", "10:00 - 18:00"),
    Doctor("Dr. John Smith", "Pediatrician", "09:00 - 14:00"),
)

val specializationList = listOf(
    Specialization("ENT", R.drawable.outline_person_apron_24),
    Specialization("Dental", R.drawable.outline_person_apron_24),
    Specialization("Cardiac Sciences", R.drawable.outline_person_apron_24),
    Specialization("Pediatric", R.drawable.outline_person_apron_24),
)


@Composable
fun DashboardScreen(navController: NavHostController,
                    viewModel: AuthViewModel = viewModel ()
) {
    val authState = viewModel.state
    LaunchedEffect (authState.isAuthenticated) {
        if (!authState.isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.DASHBOARD) { inclusive = true }
            }
        }
    }
    Scaffold(
        containerColor = Color(0xFFF0F4FF),
        bottomBar = { ApexMedBottomNavigation() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { TopHeaderSection() }
            item { SearchBar() }
            item { QuickActionGrid(navController) }
            item { MedicationChecklist(medicationList) }
            item { SpecializationsSection() }
            item { DoctorAvailableSection(availableDoctors) }

            item { LogoutButtonSection(viewModel = viewModel) }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// profile and greeting
@Composable
fun TopHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hi, Lottie Conley",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Good Afternoon!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_notifications_24),
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* notification click */ }
            )
        }
    }
}

// search
@Composable
fun SearchBar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-30).dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Find Doctor, Clinic, Medicine",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun LogoutButtonSection(viewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.logout()
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log Out",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
// action
@Composable
fun QuickActionGrid(navController: NavHostController) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        QuickActionItem("Doctor", R.drawable.outline_person_apron_24) { /* navigate doctor list */ }
        QuickActionItem("Hospital", R.drawable.outline_local_hospital_24) { /* navigate hospital list */ }
        QuickActionItem("Shop", R.drawable.outline_shopping_cart_24) { /* navigate pharmacy list */ }
        QuickActionItem("More", R.drawable.baseline_expand_more_24) { /* more */ }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun QuickActionItem(title: String, iconResId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                tint = PrimaryBlue,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 14.sp, color = Color.Gray)
    }
}

// checklist
@Composable
fun MedicationChecklist(medications: List<Medication>) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Checklist Today", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.Add, contentDescription = "Add", tint = PrimaryBlue)
        }
        Spacer(modifier = Modifier.height(12.dp))

        medications.forEach { med ->
            MedicationReminderItem(medication = med)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MedicationReminderItem(medication: Medication) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_pill_24),
                        contentDescription = "Pill icon",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(medication.name, fontWeight = FontWeight.SemiBold)
                    Text(medication.time, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (medication.taken) PrimaryBlue else Color.White)
                    .clickable { /* Toggle taken status */ }
                    .then(if (!medication.taken) Modifier.background(Color.Transparent).border(1.dp, Color.Gray, CircleShape) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (medication.taken) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_check_24),
                        contentDescription = "Taken",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


// specialization
@Composable
fun SpecializationsSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Specializations", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "See All",
                color = PrimaryBlue,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* navigate */ }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(specializationList) { spec ->
                SpecializationItem(spec = spec)
            }
        }
    }
}

@Composable
fun SpecializationItem(spec: Specialization) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = spec.iconResId),
                    contentDescription = spec.name,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = spec.name,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}



@Composable
fun DoctorAvailableSection(doctors: List<Doctor>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Doctor Available", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "Show All",
                color = PrimaryBlue,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* navigate doctors */ }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(doctors) { doctor ->
                AvailableDoctorCard(doctor = doctor)
            }
        }
    }
}

@Composable
fun AvailableDoctorCard(doctor: Doctor) {
    Card(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Dr. ${doctor.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(doctor.specialization, fontSize = 13.sp, color = Color.Gray)
                Text(doctor.time, fontSize = 13.sp, color = PrimaryBlue)
            }
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Top)
            )
        }
    }
}

// bottom navigation
@Composable
fun ApexMedBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(70.dp),
        tonalElevation = 8.dp
    ) {
        val items = listOf("Home", "Schedule", "Chat", "Profile")
        val icons = listOf(
            R.drawable.outline_home_24,
            R.drawable.baseline_schedule_24,
            R.drawable.baseline_chat_24,
            R.drawable.profile
        )

        items.forEachIndexed { index, item ->
            val isSelected = item == "Home"
            NavigationBarItem(
                selected = isSelected,
                onClick = { /* Handle navigation click */ },
                icon = {
                    Icon(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) PrimaryBlue else Color.Gray
                    )
                },
                label = {
                    Text(
                        item,
                        fontSize = 12.sp,
                        color = if (isSelected) PrimaryBlue else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}