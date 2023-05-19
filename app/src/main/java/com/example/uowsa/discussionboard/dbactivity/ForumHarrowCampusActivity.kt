package com.example.uowsa.discussionboard.dbactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.adapter.forumadapter.HarrowCampusForumAdapter
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.discussionboard.createnewforum.HarrowCampusNewForumActivity
import com.example.uowsa.usermodel.forummodel.HarrowCampusForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForumHarrowCampusActivity : AppCompatActivity() {

    var harrowCampusForumList = ArrayList<HarrowCampusForum>()
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    private var harrowCampusForumRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_harrow_campus)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        harrowCampusForumRecyclerView = findViewById(R.id.harrowCampusForumRecyclerView)
        harrowCampusForumRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getHarrowCampusForumList()


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
            val navIntentForum = Intent(this, HarrowCampusNewForumActivity::class.java)
            startActivity(navIntentForum)
        }
    }

    private fun getHarrowCampusForumList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("HarrowCampusForum")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                harrowCampusForumList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val forumList = dataSnapshot.getValue(HarrowCampusForum::class.java)
                    Log.d("tag", forumList.toString())
                    Log.d("tag1", forumList!!.harrowCampusCreatorId)
                    Log.d("tag2", forumList.harrowCampusTitle)
                    Log.d("tag3", forumList.harrowCampusDescription)
                    Log.d("tag4", forumList.harrowCampusDate)
                    Log.d("tag5", forumList.harrowCampusCreatedDateTime)
                    harrowCampusForumList.add(forumList)
                }

                val harrowCampusForumListAdapter = HarrowCampusForumAdapter(this@ForumHarrowCampusActivity, harrowCampusForumList)
                harrowCampusForumRecyclerView?.adapter = harrowCampusForumListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}