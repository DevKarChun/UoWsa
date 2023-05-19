package com.example.uowsa.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.activity.*
import com.example.uowsa.adapter.generaladapter.FriendsListAdapter
import com.example.uowsa.usermodel.generalmodel.FriendsList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class FriendsActivity : AppCompatActivity() {

    var friendsList = ArrayList<FriendsList>()
    private var friendsRecyclerView: RecyclerView? = null
    private var friendRequestCount: TextView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendRequestCount = findViewById(R.id.friendRequestCount)

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

        friendRequestCounter()

        val search = findViewById<Button>(R.id.searchFriends)
        val fRequest = findViewById<Button>(R.id.friendRequest)
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView)

//        acceptedFriendRefresh()

        search.setOnClickListener {
            val intentDiscussionBoard = Intent(this, SearchFriendsActivity::class.java)
            startActivity(intentDiscussionBoard)
        }

        fRequest.setOnClickListener {
            val intentDiscussionBoard = Intent(this, FriendRequestActivity::class.java)
            startActivity(intentDiscussionBoard)
        }

        friendsRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        getFriendsList()
    }

    private fun friendRequestCounter(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val counter = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId)
        counter.addValueEventListener(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                Log.d("tagcount", count.toString())
                if (count < 2){
                    val countFR = count.toString()
                    Log.d("tagcount2", countFR)
                    friendRequestCount?.setText("You have $countFR Friend Request")
                }else{
                    val countFRs = count.toString()
                    Log.d("tagcount1", countFRs)
                    friendRequestCount?.setText("You have $countFRs Friend Requests")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

//    private fun acceptedFriendRefresh(){
//        val user: FirebaseUser? = auth.currentUser
//        val userId: String = user!!.uid
//        friendsList.clear()
//        val readFriendsList = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId)
//        Log.d("tagfriendslist", readFriendsList.toString())
//
//        setTempData()
//        addToList()
//    }
//
//    private fun setTempData(){
//        val user: FirebaseUser? = auth.currentUser
//        val userId: String = user!!.uid
//        val readFriendData = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId)
//
//        readFriendData.addValueEventListener(object : ValueEventListener{
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach{ it ->
//                    val key: String = it.key.toString()
//                    val value: String = it.value.toString()
//                    Log.d("tagKey", key)
//                    Log.d("tagValue", value)
//
//                    val readKey = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(key)
//                    readKey.addValueEventListener(object :ValueEventListener{
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.children.forEach{
//                                val key1: String = it.key.toString()
//                                val value1: String = it.value.toString()
//                                Log.d("tagKey1", key1)
//                                Log.d("tagValue1", value1)
//
//                                val tempDataTransfer = FirebaseDatabase.getInstance().getReference("TempData").child(userId)
//
//                                tempDataTransfer.child(key).child(key1).setValue(value1)
//
////                                val deleteFriendData = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId)
////                                deleteFriendData.removeValue()
//                            }
//                        }
//                    })
//                }
//            }
//        })
//    }
//
//    private fun addToList(){
//        val user: FirebaseUser? = auth.currentUser
//        val userId: String = user!!.uid
//        val readTempData = FirebaseDatabase.getInstance().getReference("TempData").child(userId)
//        readTempData.addValueEventListener(object : ValueEventListener{
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach{
//                    val tempKey: String = it.key.toString()
//
//                    val readTempKey = FirebaseDatabase.getInstance().getReference("TempData").child(userId).child(tempKey)
//                    readTempKey.addValueEventListener(object :ValueEventListener{
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val tempKey1: String = it.key.toString()
//                            val tempValue1: String = it.value.toString()
//                            Log.d("tagTempKey1", tempKey1)
//                            Log.d("tagTempValue1", tempValue1)
//
//                        }
//                    })
//                }
//            }
//        })
//    }

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

                val friendsListAdapter = FriendsListAdapter(this@FriendsActivity, friendsList)
                Log.d("tag13", friendsListAdapter.toString())
                friendsRecyclerView?.adapter = friendsListAdapter
                Log.d("tag22", friendsRecyclerView.toString())
            }
        })
    }

}