package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DeleteAccountActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        auth = Firebase.auth

        val confirmBtn = findViewById<Button>(R.id.confirmBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)


        confirmBtn.setOnClickListener {
            deleteUoWsaAccount()
        }

        cancelBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
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

        private fun deleteUoWsaAccount() {
        val user = Firebase.auth.currentUser!!
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "User account deleted.")
            }
        }

        val userStorage = auth.currentUser?.uid.toString()
        val picName = "img"
        val filename = userStorage + picName
        val storageRef = FirebaseStorage.getInstance().getReference("images").child(FirebaseAuth.getInstance().currentUser!!.uid).child(filename)

        storageRef.delete().addOnSuccessListener {
            Log.d("tag", "User Storage Deleted")
        }.addOnFailureListener {
            Log.d("tag", "Unable to delete user storage")
        }

        val userId: String = user.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        databaseReference.removeValue()

        val intentMain = Intent(this, MainActivity::class.java)
        Toast.makeText(this@DeleteAccountActivity, "Your account has now been closed", Toast.LENGTH_SHORT).show()
        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentMain)
        finish()
    }
}