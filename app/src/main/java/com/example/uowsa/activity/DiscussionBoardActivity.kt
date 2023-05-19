package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.uowsa.*
import com.example.uowsa.discussionboard.dbactivity.DBGeneralActivity
import com.example.uowsa.discussionboard.dbactivity.DBMakeNewFriendsActivity
import com.example.uowsa.discussionboard.dbactivity.DBStudentWellbeingActivity
import com.example.uowsa.discussionboard.dbactivity.ForumAccommodationActivity
import com.example.uowsa.discussionboard.dbactivity.ForumCavendishCampusActivity
import com.example.uowsa.discussionboard.dbactivity.ForumClubsAndSocietiesActivity
import com.example.uowsa.discussionboard.dbactivity.ForumEventsActivity
import com.example.uowsa.discussionboard.dbactivity.ForumHarrowCampusActivity
import com.example.uowsa.discussionboard.dbactivity.ForumMaryleboneCampusActivity
import com.example.uowsa.discussionboard.dbactivity.ForumOtherActivity
import com.example.uowsa.discussionboard.dbactivity.ForumRegentsCampusActivity
import com.example.uowsa.discussionboard.dbactivity.ForumStudentUnionActivity
import com.example.uowsa.discussionboard.dbactivity.ForumStudyActivity

class DiscussionBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion_board)

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

        val generalBtn = findViewById<Button>(R.id.generalBtn)
        val studyBtn = findViewById<Button>(R.id.studyBtn)
        val makeNewFriendsBtn = findViewById<Button>(R.id.makeNewFriendsBtn)
        val eventsBtn = findViewById<Button>(R.id.eventsBtn)
        val accommodationBtn = findViewById<Button>(R.id.accommodationBtn)
        val studentUnionBtn = findViewById<Button>(R.id.studentUnionBtn)
        val clubsAndSocietiesBtn = findViewById<Button>(R.id.clubsAndSocietiesBtn)
        val studentWellbeingBtn = findViewById<Button>(R.id.studentWellbeingBtn)
        val cavendishCampusBtn = findViewById<Button>(R.id.cavendishCampusBtn)
        val harrowCampusBtn = findViewById<Button>(R.id.harrowCampusBtn)
        val regentsCampusBtn = findViewById<Button>(R.id.regentsCampusBtn)
        val maryleboneCampusBtn = findViewById<Button>(R.id.maryleboneCampusBtn)
        val otherBtn = findViewById<Button>(R.id.otherBtn)

        generalBtn.setOnClickListener {
            val generalIntent = Intent(this, DBGeneralActivity::class.java)
            startActivity(generalIntent)
        }

        studyBtn.setOnClickListener {
            val studyIntent = Intent(this, ForumStudyActivity::class.java)
            startActivity(studyIntent)
        }

        makeNewFriendsBtn.setOnClickListener {
            val makeNewFriendsIntent = Intent(this, DBMakeNewFriendsActivity::class.java)
            startActivity(makeNewFriendsIntent)
        }

        eventsBtn.setOnClickListener {
            val eventsIntent = Intent(this, ForumEventsActivity::class.java)
            startActivity(eventsIntent)
        }

        accommodationBtn.setOnClickListener {
            val accommodationIntent = Intent(this, ForumAccommodationActivity::class.java)
            startActivity(accommodationIntent)
        }

        studentUnionBtn.setOnClickListener {
            val studentUnionIntent = Intent(this, ForumStudentUnionActivity::class.java)
            startActivity(studentUnionIntent)
        }

        clubsAndSocietiesBtn.setOnClickListener {
            val clubsAndSocietiesIntent = Intent(this, ForumClubsAndSocietiesActivity::class.java)
            startActivity(clubsAndSocietiesIntent)
        }

        studentWellbeingBtn.setOnClickListener {
            val studentWellbeingIntent = Intent(this, DBStudentWellbeingActivity::class.java)
            startActivity(studentWellbeingIntent)
        }

        cavendishCampusBtn.setOnClickListener {
            val cavendishCampusIntent = Intent(this, ForumCavendishCampusActivity::class.java)
            startActivity(cavendishCampusIntent)
        }

        harrowCampusBtn.setOnClickListener {
            val harrowCampusIntent = Intent(this, ForumHarrowCampusActivity::class.java)
            startActivity(harrowCampusIntent)
        }

        regentsCampusBtn.setOnClickListener {
            val regentsCampusIntent = Intent(this, ForumRegentsCampusActivity::class.java)
            startActivity(regentsCampusIntent)
        }

        maryleboneCampusBtn.setOnClickListener {
            val maryleboneCampusIntent = Intent(this, ForumMaryleboneCampusActivity::class.java)
            startActivity(maryleboneCampusIntent)
        }

        otherBtn.setOnClickListener {
            val otherIntent = Intent(this, ForumOtherActivity::class.java)
            startActivity(otherIntent)
        }
    }
}