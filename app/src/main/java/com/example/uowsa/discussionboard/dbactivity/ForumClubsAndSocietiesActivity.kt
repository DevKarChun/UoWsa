package com.example.uowsa.discussionboard.dbactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.adapter.forumadapter.ClubsAndSocietiesForumAdapter
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.discussionboard.createnewforum.ClubsAndSocietiesNewForumActivity
import com.example.uowsa.usermodel.forummodel.ClubsAndSocietiesForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForumClubsAndSocietiesActivity : AppCompatActivity() {

    var clubsAndSocietiesList = ArrayList<ClubsAndSocietiesForum>()
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    private var clubsAndSocietiesForumRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_clubs_and_societies)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        clubsAndSocietiesForumRecyclerView = findViewById(R.id.clubsAndSocietiesForumRecyclerView)
        clubsAndSocietiesForumRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        clubsAndSocietiesForumList()


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

        val newForum = findViewById<ImageButton>(R.id.createNewForumBtn)

        newForum.setOnClickListener {
            val navIntentForum = Intent(this, ClubsAndSocietiesNewForumActivity::class.java)
            startActivity(navIntentForum)
        }
    }

    private fun clubsAndSocietiesForumList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ClubsAndSocietiesForum")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubsAndSocietiesList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val forumList = dataSnapshot.getValue(ClubsAndSocietiesForum::class.java)
                    Log.d("tag", forumList.toString())
                    Log.d("tag1", forumList!!.clubsAndSocietiesCreatorId)
                    Log.d("tag2", forumList.clubsAndSocietiesTitle)
                    Log.d("tag3", forumList.clubsAndSocietiesDescription)
                    Log.d("tag4", forumList.clubsAndSocietiesDate)
                    Log.d("tag5", forumList.clubsAndSocietiesCreatedDateTime)
                    clubsAndSocietiesList.add(forumList)
                }

                val clubsAndSocietiesForumListAdapter = ClubsAndSocietiesForumAdapter(this@ForumClubsAndSocietiesActivity, clubsAndSocietiesList)
                clubsAndSocietiesForumRecyclerView?.adapter = clubsAndSocietiesForumListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}