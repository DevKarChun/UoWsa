package com.example.uowsa.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.uowsa.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth

        val discussionBoard = findViewById<Button>(R.id.discussionBoard)
        val friends = findViewById<Button>(R.id.friends)
        val uowMessage = findViewById<Button>(R.id.uowMessage)
        val studentUnion = findViewById<Button>(R.id.studentUnion)
        val clubsAndSociety = findViewById<Button>(R.id.clubsAndSocieties)
        val studentWellBeing = findViewById<Button>(R.id.studentWellbeing)
        val profile = findViewById<Button>(R.id.profile)
        val settings = findViewById<Button>(R.id.settings)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)

        discussionBoard.setOnClickListener {
            val intentDiscussionBoard = Intent(this, DiscussionBoardActivity::class.java)
            startActivity(intentDiscussionBoard)
        }

        friends.setOnClickListener {
            val intentFriends = Intent(this, FriendsActivity::class.java)
            startActivity(intentFriends)
        }

        uowMessage.setOnClickListener {
            val intentUoWMessage = Intent(this, UoWMessageActivity::class.java)
            startActivity(intentUoWMessage)
        }

        studentUnion.setOnClickListener {
            val intentStudentUnion = Intent(this, StudentUnionActivity::class.java)
            startActivity(intentStudentUnion)
        }

        clubsAndSociety.setOnClickListener {
            val intentClubsAndSocieties = Intent(this, StudentCommunityActivity::class.java)
            startActivity(intentClubsAndSocieties)
        }

        studentWellBeing.setOnClickListener {
            val intentClubsAndSocieties = Intent(this, StudentWellbeingActivity::class.java)
            startActivity(intentClubsAndSocieties)
        }

        profile.setOnClickListener {
            val intentProfile = Intent(this, ProfileActivity::class.java)
            startActivity(intentProfile)
        }

        settings.setOnClickListener {
            val intentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(intentSettings)
        }



        homeBtn.setOnClickListener{
            val navIntentHome = Intent(this, HomeActivity::class.java)
            startActivity(navIntentHome)
        }

        profileBtn.setOnClickListener {
            val navIntentProfile = Intent(this, ProfileActivity::class.java)
            startActivity(navIntentProfile)
        }

        uowMessageBtn.setOnClickListener {
            val navIntentUoWMessage = Intent(this, UoWMessageActivity::class.java)
            startActivity(navIntentUoWMessage)
        }

        settingsBtn.setOnClickListener {
            val navIntentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(navIntentSettings)
        }

        logoutBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTheme)
            val dialog: AlertDialog = builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Cancel") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("Logout") { dialog, _ ->
                    val database = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid).child("connectivityStatus").setValue("Offline")
                    signOut()
                    dialog.dismiss()
                }
                .create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        imageExists()
    }

    private fun imageExists() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val databaseImage = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profileImage")
        databaseImage.get().addOnSuccessListener {
            val imageExist = it.value.toString()
            if (imageExist == ""){
                val storageRef = FirebaseStorage.getInstance().getReference("appImage").child("ic_launcher.png")
                storageRef.downloadUrl.addOnSuccessListener {task->
                    val url = task.toString()
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    databaseReference.child("profileImage").setValue(url)
                }
            }
        }
    }

    private fun signOut(){
        auth.signOut()
        val intentHome = Intent(this, MainActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentHome)
        finish()
    }
}
