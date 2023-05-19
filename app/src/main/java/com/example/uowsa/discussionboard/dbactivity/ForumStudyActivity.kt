package com.example.uowsa.discussionboard.dbactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.discussionboard.createnewforum.StudyNewForumActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.adapter.forumadapter.StudyForumAdapter
import com.example.uowsa.usermodel.forummodel.StudyForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ForumStudyActivity : AppCompatActivity() {

    var studyForumList = ArrayList<StudyForum>()
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    private var studyForumRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_study)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        studyForumRecyclerView = findViewById(R.id.studyForumRecyclerView)
        studyForumRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        getStudyForumList()

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
            val navIntentForum = Intent(this, StudyNewForumActivity::class.java)
            startActivity(navIntentForum)
        }

    }

    private fun getStudyForumList() {

        databaseReference = FirebaseDatabase.getInstance().getReference("StudyForum")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studyForumList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val forumList = dataSnapshot.getValue(StudyForum::class.java)
                    Log.d("tag", forumList.toString())
                    Log.d("tag1", forumList!!.creatorId)
                    Log.d("tag2", forumList.studyTitle)
                    Log.d("tag3", forumList.studyDescription)
                    Log.d("tag4", forumList.studyDate)
                    Log.d("tag5", forumList.createdDateTime)
                    studyForumList.add(forumList)
                }

                val studyForumListAdapter = StudyForumAdapter(this@ForumStudyActivity, studyForumList)
                studyForumRecyclerView?.adapter = studyForumListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}