package com.example.uowsa.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val username = findViewById<EditText>(R.id.username)
        val usernameSetText = findViewById<EditText>(R.id.username)
        val status = findViewById<EditText>(R.id.statusDescription)
        val statusSetText = findViewById<EditText>(R.id.statusDescription)
        val campusLocation = findViewById<EditText>(R.id.campusLocation)
        val campusLocationSetText = findViewById<EditText>(R.id.campusLocation)
        val studyCourse = findViewById<EditText>(R.id.studyCourse)
        val studyCourseSetText = findViewById<EditText>(R.id.studyCourse)
        val updateBtn = findViewById<Button>(R.id.update)

        val navHome = findViewById<ImageButton>(R.id.navHomeBtn)
        val navProfile = findViewById<ImageButton>(R.id.navProfileBtn)
        val navMessage = findViewById<ImageButton>(R.id.navUowMessage)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)

        auth = Firebase.auth

        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val usernameData = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName")
        usernameData.get().addOnSuccessListener {
            val name = it.value.toString()
            Log.d("tag", name)
            usernameSetText.setText(name)
        }

        val statusData = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status")
        statusData.get().addOnSuccessListener {
            val statusInfo = it.value.toString()
            Log.d("tag", statusInfo)
            if (statusInfo != "Hey, UoWsa is the future!"){
                statusSetText.setText(statusInfo)
            }
        }

        val campusLocationData = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("campusLocation")
        campusLocationData.get().addOnSuccessListener {
            val campusLocationInfo = it.value.toString()
            Log.d("tag", campusLocationInfo)
            if (campusLocationInfo != ""){
                campusLocationSetText.setText(campusLocationInfo)
            }
        }

        val studyCourseData = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("studyCourse")
        studyCourseData.get().addOnSuccessListener {
            val studyCourseDataInfo = it.value.toString()
            Log.d("tag", studyCourseDataInfo)
            if (studyCourseDataInfo != ""){
                studyCourseSetText.setText(studyCourseDataInfo)
            }
        }

        updateBtn.setOnClickListener{
            val usernameLowerCase = username.text.toString().lowercase()
            val usernameTxt = username.text.toString()
            val campusLocationTxt = campusLocation.text.toString()
            val studyCourseTxt = studyCourse.text.toString()
            if (username.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }else if (usernameTxt.length < 4 || usernameTxt.length > 12){
                Toast.makeText(this@UpdateProfileActivity, "Please enter a username between 4 - 12 characters", Toast.LENGTH_SHORT).show()
            } else if (usernameLowerCase == "admin"){

                    Toast.makeText(this@UpdateProfileActivity, "This username $usernameTxt is reserved, please enter a new username.", Toast.LENGTH_SHORT).show()
            }else
            {
                var statusTxt = status.text.toString()
                if (status.text.toString().isEmpty()){
                    statusTxt = "Hey, UoWsa is the future!"
                    updateData(usernameTxt, statusTxt, campusLocationTxt, studyCourseTxt)
                }else if (statusTxt.length > 50){
                    Toast.makeText(this@UpdateProfileActivity, "Your status is over 50 characters", Toast.LENGTH_SHORT).show()
                }else{
                    updateData(usernameTxt, statusTxt, campusLocationTxt, studyCourseTxt)
                }
            }
        }

        navHome.setOnClickListener{
            val navIntentHome = Intent(this, HomeActivity::class.java)
            startActivity(navIntentHome)
        }

        navProfile.setOnClickListener {
            val navIntentProfile = Intent(this, ProfileActivity::class.java)
            startActivity(navIntentProfile)
        }

        navMessage.setOnClickListener {
            val navIntentUoWMessage = Intent(this, UoWMessageActivity::class.java)
            startActivity(navIntentUoWMessage)
        }

        navSettings.setOnClickListener {
            val navIntentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(navIntentSettings)
        }
    }


    private fun updateData(username: String, status: String, campusLocation: String, studyCourse: String) {
        setUsername(username)
        setStatus(status)
        setCampusLocation(campusLocation)
        setStudyCourse(studyCourse)
        val intentHome = Intent(this, ProfileActivity::class.java)
        startActivity(intentHome)
    }

    private fun setUsername(username: String){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName")

        databaseReference.setValue(username).addOnCompleteListener(this){
            if (it.isSuccessful){
                Toast.makeText(this, "Username Updated Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Username failed to Update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setStatus(status: String){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status")

        databaseReference.setValue(status).addOnCompleteListener(this){
            if (it.isSuccessful){
                Toast.makeText(this, "Status Updated Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Status failed to Update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCampusLocation(campusLocation: String){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("campusLocation")

        databaseReference.setValue(campusLocation).addOnCompleteListener(this){
            if (it.isSuccessful){

            }else{
                Toast.makeText(this, "Campus Location failed to Update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setStudyCourse(studyCourse: String){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("studyCourse")

        databaseReference.setValue(studyCourse).addOnCompleteListener(this){
            if (it.isSuccessful){

            }else{
                Toast.makeText(this, "Study Course failed to Update", Toast.LENGTH_SHORT).show()
            }
        }
    }
}