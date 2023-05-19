package com.example.uowsa.discussionboard.dbactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.adapter.forumadapter.OtherForumAdapter
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.discussionboard.createnewforum.OtherNewForumActivity
import com.example.uowsa.usermodel.forummodel.OtherForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForumOtherActivity : AppCompatActivity() {
    var otherForumList = ArrayList<OtherForum>()
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    private var otherForumRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_other)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        otherForumRecyclerView = findViewById(R.id.otherForumRecyclerView)
        otherForumRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getOtherForumList()


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
            val navIntentForum = Intent(this, OtherNewForumActivity::class.java)
            startActivity(navIntentForum)
        }
    }

    private fun getOtherForumList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("OtherForum")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                otherForumList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val forumList = dataSnapshot.getValue(OtherForum::class.java)
                    Log.d("tag", forumList.toString())
                    Log.d("tag1", forumList!!.otherCreatorId)
                    Log.d("tag2", forumList.otherTitle)
                    Log.d("tag3", forumList.otherDescription)
                    Log.d("tag4", forumList.otherDate)
                    Log.d("tag5", forumList.otherCreatedDateTime)
                    otherForumList.add(forumList)
                }

                val otherForumListAdapter = OtherForumAdapter(this@ForumOtherActivity, otherForumList)
                otherForumRecyclerView?.adapter = otherForumListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}