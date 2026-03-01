package com.example.doctors.model

import androidx.annotation.DrawableRes
import com.example.doctors.R

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
    @DrawableRes val imageRes: Int
)

// List of available specialists
val allSpecialistsList = listOf(
    Specialist("1", "Dr. Andrew Jamison", "Neurologist", "4.8", "MBBS", "$120", "1500+", "8 Years", "Expert in brain disorders...", "Fellowship in USA", R.drawable.img_doctor_welcome),
    Specialist("2", "Dr. Sarah Konor", "Dentist", "4.9", "DDS", "$100", "1200+", "5 Years", "Specialist in cosmetic...", "Orthodontic Expert", R.drawable.sarahdoctor),
    Specialist("3", "Dr. John Doe", "Psychologist", "4.7", "PhD", "$150", "800+", "10 Years", "Focuses on mental health...", "Clinical Psychologist", R.drawable.johndoctor),
    Specialist("4", "Dr. Mike Tyson", "ENT", "4.5", "MBBS", "$90", "2000+", "12 Years", "Specialist in Ear, Nose...", "Surgeon", R.drawable.mikedoctor)
)