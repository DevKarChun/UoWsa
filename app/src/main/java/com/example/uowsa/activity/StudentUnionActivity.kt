package com.example.uowsa.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.uowsa.*

class StudentUnionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_union)

        val facebook = findViewById<ImageButton>(R.id.facebook)
        val twitter = findViewById<ImageButton>(R.id.twitter)
        val instagram = findViewById<ImageButton>(R.id.instagram)
        val linkedin = findViewById<ImageButton>(R.id.linkedin)
        val youtube = findViewById<ImageButton>(R.id.youtube)

        val navHome = findViewById<ImageButton>(R.id.navHomeBtn)
        val navProfile = findViewById<ImageButton>(R.id.navProfileBtn)
        val navMessage = findViewById<ImageButton>(R.id.navUowMessage)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)

        val facebookUrl = "https://www.facebook.com/WestminsterSU/"
        val twitterUrl = "https://twitter.com/WestminsterSU"
        val instagramUrl = "https://www.instagram.com/westminstersu/"
        val linkedinUrl = "https://www.linkedin.com/school/westminster-students-union/"
        val youtubeUrl = "https://www.youtube.com/channel/UC8vfOlSxcePnvRZ8FbQ2yrw"

        facebook.setOnClickListener {
            val intentFacebook = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
            startActivity(intentFacebook)
        }

        twitter.setOnClickListener {
            val intentTwitter = Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl))
            startActivity(intentTwitter)
        }

        instagram.setOnClickListener {
            val intentInstagram = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
            startActivity(intentInstagram)
        }

        linkedin.setOnClickListener {
            val intentLinkedin = Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl))
            startActivity(intentLinkedin)
        }

        youtube.setOnClickListener {
            val intentYoutube = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
            startActivity(intentYoutube)
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
}