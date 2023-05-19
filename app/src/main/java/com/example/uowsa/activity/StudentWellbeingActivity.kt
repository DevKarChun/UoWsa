package com.example.uowsa.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.uowsa.R

class StudentWellbeingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_wellbeing)

        val wellbeingResourcePage = findViewById<Button>(R.id.wellbeingResourceBtn)
        val feelGoodApp = findViewById<Button>(R.id.feelGoodBtn)
        val reportAndSupport = findViewById<Button>(R.id.reportAndSupportBtn)
        val fiveWaysToWellbeing = findViewById<Button>(R.id.fiveWaysToWellbeingBtn)
        val disabilityLearningSupport = findViewById<Button>(R.id.disabilityLearningSupport)

        val wellbeingResourcePageUrl = "https://www.westminster.ac.uk/current-students/support-and-services/student-wellbeing/wellbeing-resources"
        val feelGoodAppUrl = "https://www.westminster.ac.uk/current-students/support-and-services/student-wellbeing/wellbeing-resources/feeling-good-app"
        val reportAndSupportUrl = "https://reportandsupport.westminster.ac.uk/"
        val fiveWaysToWellbeingUrl = "https://www.youtube.com/watch?v=juggl6Z36ZY&ab_channel=UniversityofWestminster"
        val disabilityLearningSupportUrl = "https://www.westminster.ac.uk/current-students/support-and-services/disability-learning-support"

        wellbeingResourcePage.setOnClickListener {
            val intentWellbeingResourcePage = Intent(Intent.ACTION_VIEW, Uri.parse(wellbeingResourcePageUrl))
            startActivity(intentWellbeingResourcePage)
        }

        feelGoodApp.setOnClickListener {
            val intentFeelGoodApp = Intent(Intent.ACTION_VIEW, Uri.parse(feelGoodAppUrl))
            startActivity(intentFeelGoodApp)
        }

        reportAndSupport.setOnClickListener {
            val intentReportAndSupport = Intent(Intent.ACTION_VIEW, Uri.parse(reportAndSupportUrl))
            startActivity(intentReportAndSupport)
        }

        fiveWaysToWellbeing.setOnClickListener {
            val intentFiveWaysToWellbeing = Intent(Intent.ACTION_VIEW, Uri.parse(fiveWaysToWellbeingUrl))
            startActivity(intentFiveWaysToWellbeing)
        }

        disabilityLearningSupport.setOnClickListener {
            val intentDisabilityLearningSupport = Intent(Intent.ACTION_VIEW, Uri.parse(disabilityLearningSupportUrl))
            startActivity(intentDisabilityLearningSupport)
        }

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
    }
}