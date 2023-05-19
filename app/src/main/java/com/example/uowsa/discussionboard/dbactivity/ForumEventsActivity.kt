package com.example.uowsa.discussionboard.dbactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.adapter.forumadapter.EventsForumAdapter
import com.example.uowsa.discussionboard.createnewforum.EventsNewForumActivity
import com.example.uowsa.usermodel.forummodel.EventsForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ForumEventsActivity : AppCompatActivity() {

    var eventsForumList = ArrayList<EventsForum>()
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    private var eventsForumRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_events)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        eventsForumRecyclerView = findViewById(R.id.eventsForumRecyclerView)
        eventsForumRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        getEventsForumList()

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
            val navIntentForum = Intent(this, EventsNewForumActivity::class.java)
            startActivity(navIntentForum)
        }
    }

    private fun getEventsForumList() {

        databaseReference = FirebaseDatabase.getInstance().getReference("EventsForum")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsForumList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val forumList = dataSnapshot.getValue(EventsForum::class.java)
                    Log.d("tag", forumList.toString())
                    Log.d("tag1", forumList!!.eventsCreatorId)
                    Log.d("tag2", forumList.eventsTitle)
                    Log.d("tag3", forumList.eventsDescription)
                    Log.d("tag4", forumList.eventsDate)
                    Log.d("tag5", forumList.eventsCreatedDateTime)
                    eventsForumList.add(forumList)
                }

                val eventsForumListAdapter = EventsForumAdapter(this@ForumEventsActivity, eventsForumList)
                eventsForumRecyclerView?.adapter = eventsForumListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}