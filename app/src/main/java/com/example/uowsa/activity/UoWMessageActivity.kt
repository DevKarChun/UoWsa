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
import com.example.uowsa.adapter.generaladapter.FriendListMessageAdapter
import com.example.uowsa.usermodel.generalmodel.FriendsList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class UoWMessageActivity : AppCompatActivity() {

    var friendsList = ArrayList<FriendsList>()
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var friendsMessageRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uowmessage)

        friendsMessageRecyclerView = findViewById(R.id.friendsMessageRecyclerView)

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

        auth = Firebase.auth

        friendsMessageRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getFriendsList()

//        val messageCounter = findViewById<TextView>(R.id.messageCounter)
//        val user: FirebaseUser? = auth.currentUser
//        val userId: String = user!!.uid
//        val messageCounterDatabase = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId)
//
//        messageCounterDatabase.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach{it->
//                    val keyFriendsList = it.key.toString()
//                    Log.d("tag111", keyFriendsList)
//                    var unreadMessageCount: Long = 0
//                    messageCounterDatabase.child(keyFriendsList).orderByChild("messageStatus").equalTo("unread").addValueEventListener(object : ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val unreadCount = snapshot.childrenCount
//                            if (unreadCount > 0){
//                                unreadMessageCount +=1
//                            }
////                            unreadMessageCount =+ unreadCount
//                            Log.d("tagunreadcount", unreadCount.toString())
//                            Log.d("tagunreadMessageCount", unreadMessageCount.toString())
//
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//
//                    })
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })
//        if (unreadMessageCount > 0){
//            val unreadMessages = unreadMessageCount.toString()
//            messageCounter.setText(unreadMessages)
//            messageCounter.visibility = View.VISIBLE
//        }
    }

    private fun getFriendsList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val friends = dataSnapShot.getValue(FriendsList::class.java)
                    Log.d("tag12", friends.toString())
                    friendsList.add(friends!!)
                }

                val friendListMessageAdapter = FriendListMessageAdapter(this@UoWMessageActivity, friendsList)
                Log.d("tag13", friendListMessageAdapter.toString())
                friendsMessageRecyclerView?.adapter = friendListMessageAdapter
                Log.d("tag22", friendsMessageRecyclerView.toString())
            }
        })
    }
}