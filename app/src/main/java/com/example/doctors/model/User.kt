package com.example.doctors.model

data class User(
    val userId : String = "",
    val email : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val contact : String = "",
    val dob : String = "",
    val isDoctor: Boolean = false
){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "userId" to userId,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "contact" to contact,
            "dob" to dob,
            "isDoctor" to isDoctor
        )
    }
}