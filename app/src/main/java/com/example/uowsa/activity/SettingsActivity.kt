package com.example.uowsa.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth

        val updateProfileBtn = findViewById<Button>(R.id.updateProfile)
        val helpCenter = findViewById<Button>(R.id.helpCenter)
        val contactUs = findViewById<Button>(R.id.contactUs)
        val aboutUs = findViewById<Button>(R.id.aboutUsBtn)
        val tos = findViewById<Button>(R.id.termsOfServiceBtn)
        val blockedList = findViewById<Button>(R.id.blockedListBtn)
        val deleteAccount = findViewById<Button>(R.id.deleteAccountBtn)
        val logout = findViewById<Button>(R.id.logoutSetBtn)
        val admin = findViewById<Button>(R.id.contactAdmin)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)


        updateProfileBtn.setOnClickListener {
            val intentHome = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intentHome)
        }

        helpCenter.setOnClickListener {
            val intentHome = Intent(this, HelpCenterActivity::class.java)
            startActivity(intentHome)
        }

        admin.setOnClickListener{
            val intentHome = Intent(this, ContactAdminActivity::class.java)
            startActivity(intentHome)
        }

        contactUs.setOnClickListener {
            contactEmail()
        }

        aboutUs.setOnClickListener {
            val intentHome = Intent(this, AboutUsActivity::class.java)
            startActivity(intentHome)
        }

        tos.setOnClickListener {
            val intentHome = Intent(this, ToSActivity::class.java)
            startActivity(intentHome)
        }

        blockedList.setOnClickListener {
            val intentHome = Intent(this, BlockedListActivity::class.java)
            startActivity(intentHome)
        }

        deleteAccount.setOnClickListener {
            val intentHome = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intentHome)
//            deleteUoWsaAccount()
        }

        logout.setOnClickListener {
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
    }

//    private fun deleteUoWsaAccount() {
//        val user = Firebase.auth.currentUser!!
//        user.delete().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("TAG", "User account deleted.")
//            }
//        }
//
//        val userStorage = auth.currentUser?.uid.toString()
//        val picName = "img"
//        val filename = userStorage + picName
//        val storageRef = FirebaseStorage.getInstance().getReference("images").child(FirebaseAuth.getInstance().currentUser!!.uid).child(filename)
//
//        storageRef.delete().addOnSuccessListener {
//            Log.d("tag", "User Storage Deleted")
//        }.addOnFailureListener {
//            Log.d("tag", "Unable to delete user storage")
//        }
//
//
//        val userId: String = user.uid
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
//        databaseReference.removeValue()
//
//        val intentMain = Intent(this, MainActivity::class.java)
//        Toast.makeText(this@SettingsActivity, "Your account has now been closed", Toast.LENGTH_SHORT).show()
//        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intentMain)
//        finish()
//    }

    private fun contactEmail(){
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "w1807769@my.westminster.ac.uk", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun signOut(){
        auth.signOut()
        val intentHome = Intent(this, MainActivity::class.java)
        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentHome)
        finish()
    }

}