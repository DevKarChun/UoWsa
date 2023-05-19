package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.adapter.generaladapter.BlockedUsersAdapter
import com.example.uowsa.usermodel.generalmodel.BlockedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class BlockedListActivity : AppCompatActivity() {

    var blockedList = ArrayList<BlockedList>()
    private var blockedUsersRecyclerView: RecyclerView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_list)

        auth = Firebase.auth

        blockedUsersRecyclerView = findViewById(R.id.blockedUsersRecyclerView)

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

        blockedUsersRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getBlockedList()
    }

    private fun getBlockedList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("BlockedList").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                blockedList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val blocked = dataSnapShot.getValue(BlockedList::class.java)
                    Log.d("tag12", blocked.toString())
                    blockedList.add(blocked!!)
                }

                val friendsListAdapter = BlockedUsersAdapter(this@BlockedListActivity, blockedList)
                Log.d("tag13", friendsListAdapter.toString())
                blockedUsersRecyclerView?.adapter = friendsListAdapter
                Log.d("tag22", blockedUsersRecyclerView.toString())
            }
        })
    }
}