package com.example.uowsa.discussionboard.createnewforum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.uowsa.discussionboard.dbactivity.ForumMaryleboneCampusActivity
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MaryleboneCampusNewForumActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marylebone_campus_new_forum)

        auth = Firebase.auth

        val titleTxt = findViewById<EditText>(R.id.titleTxt)
        val descriptionTxt = findViewById<EditText>(R.id.descriptionTxt)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

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

        submitBtn.setOnClickListener {
            if (titleTxt.text.toString().isEmpty()){
                Toast.makeText(this, "Title field is empty", Toast.LENGTH_SHORT).show()
            }else if (descriptionTxt.text.toString().isEmpty()){
                Toast.makeText(this, "The description field is empty", Toast.LENGTH_SHORT).show()
            }else{
                val title = titleTxt.text.toString()
                val description = descriptionTxt.text.toString()
                Log.d("tag1", title)
                Log.d("tag1", description)

                createMaryleboneCampusForum(title, description)
                val intentStudy = Intent(this@MaryleboneCampusNewForumActivity, ForumMaryleboneCampusActivity::class.java)
                intentStudy.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentStudy)
                finish()
            }
        }
    }

    private fun createMaryleboneCampusForum(title: String, description: String) {

        Log.d("tag", "testing")
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        Log.d("tag", userId)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.now().format(formatter)
        val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDateTime.now().format(format)
        Log.d("tag1", userId)

        val databaseRef = FirebaseDatabase.getInstance().getReference("MaryleboneCampusForum")
        Log.d("tagdatabaseref", databaseRef.toString())

        val newPost : HashMap <String, String> = HashMap()
        newPost["maryleboneCampusCreatorId"] = userId
        newPost["maryleboneCampusTitle"] = title
        newPost["maryleboneCampusDescription"] = description
        newPost["maryleboneCampusDate"] = date
        newPost["maryleboneCampusCreatedDateTime"] = dateTime

        Log.d("tag1", title)
        Log.d("tag1", description)
        Log.d("tag1", date)
        Log.d("tag1", dateTime)

        Log.d("tag", newPost.toString())

        databaseRef.push().setValue(newPost)
    }
}