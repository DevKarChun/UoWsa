package com.example.uowsa.usermodel.generalmodel

data class User(var userId:String = "",
                val userName:String = "",
                val profileImage:String = "",
                val status:String = "",
                val studyCourse: String = "",
                val campusLocation: String = "")