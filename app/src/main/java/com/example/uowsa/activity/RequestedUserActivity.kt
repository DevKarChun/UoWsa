package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class RequestedUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var acceptFriendBtn: Button? = null
    private var declineFriendBtn: Button? = null
    private var senderUserId: String? = null
    private var receiverFriendUserId: String? = null
    private var friendAccepted: String? = null
    private var optional1: TextView? = null
    private var optional2: TextView? = null
    private var studyCourse: String? = null
    private var campusLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requested_user)

        auth = Firebase.auth

        val txtUsername = findViewById<TextView>(R.id.userName)
        val txtStatus = findViewById<TextView>(R.id.status)
        val userImage = findViewById<CircleImageView>(R.id.userImage)
        acceptFriendBtn = findViewById(R.id.acceptFriendBtn)
        declineFriendBtn = findViewById(R.id.declineFriendRequestBtn)
        optional1 = findViewById(R.id.optional1)
        optional2 = findViewById(R.id.optional2)

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

        senderUserId = intent.getStringExtra("senderUserId").toString()
        receiverFriendUserId = intent.getStringExtra("receiverFriendUserId").toString()
//        senderUserName = intent.getStringExtra("senderUserName").toString()
//        senderProfileImage = intent.getStringExtra("senderProfileImage").toString()
//        senderStatus = intent.getStringExtra("senderStatus").toString()
        friendAccepted = intent.getStringExtra("friendAccepted").toString()

        Log.d("tag", senderUserId!!)
        Log.d("tag", receiverFriendUserId!!)
//        Log.d("tag", senderUserName!!)
//        Log.d("tag", senderProfileImage!!)
//        Log.d("tag", senderStatus!!)
        Log.d("tag", friendAccepted!!)

        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(senderUserId!!)
        databaseRefresh.child("userName").get().addOnSuccessListener {
            val usernameRefresh = it.value.toString()
            txtUsername.setText(usernameRefresh)
        }
        databaseRefresh.child("status").get().addOnSuccessListener {
            val statusRefresh = it.value.toString()
            txtStatus.setText(statusRefresh)
        }
        databaseRefresh.child("profileImage").get().addOnSuccessListener {
            val imageRefresh = it.value.toString()
            Glide.with(this).load(imageRefresh).into(userImage)
        }

        val acceptFriendOptionals = FirebaseDatabase.getInstance().getReference("Users").child(senderUserId!!)
        acceptFriendOptionals.child("studyCourse").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                studyCourse = snapshot.getValue().toString()
                optional1?.setText(studyCourse)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        acceptFriendOptionals.child("campusLocation").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                campusLocation = snapshot.getValue().toString()
                if (studyCourse == ""){
                    optional1?.setText(campusLocation)
                }else{
                    optional2?.setText(campusLocation)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
//        acceptFriendOptionals.child("studyCourse").get().addOnSuccessListener { it ->
//            val studyCourse = it.value.toString()
//            if (studyCourse == ""){
////                optional1?.setText(studyCourse)
//                acceptFriendOptionals.child("campusLocation").get().addOnSuccessListener{
//                    val campusLocation = it.value.toString()
//                    optional1?.setText(campusLocation)
//                }
//            }else{
//                optional1?.setText(studyCourse)
//                acceptFriendOptionals.child("campusLocation").get().addOnSuccessListener{
//                    val campusLocation = it.value.toString()
//                    optional2?.setText(campusLocation)
//                }
//            }
//        }
//        acceptFriendOptionals.child("campusLocation").get().addOnSuccessListener {
//            val campusLocation = it.value.toString()
//            if (optional1?.text == ""){
//                optional1?.setText(campusLocation)
//            }else{
//                optional2?.setText(campusLocation)
//            }
//        }
        optional1?.visibility = View.VISIBLE
        optional2?.visibility = View.VISIBLE

        acceptFriendBtn?.setOnClickListener {
            addToFriendList()
        }

        declineFriendBtn?.setOnClickListener {
            declineFriendList()
        }
    }



    private fun addToFriendList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        if(receiverFriendUserId == userId){
            val addFriendToUserFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(senderUserId!!)

            addFriendToUserFriendList.child("friendUserId").setValue(senderUserId!!)

            addFriendToUserFriendList.child("friends").setValue("Friend")

            val addFriendToOtherFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(senderUserId!!).child(userId)

            addFriendToOtherFriendList.child("friendUserId").setValue(userId)

            addFriendToOtherFriendList.child("friends").setValue("Friend")

            friendRequestAccepted()

        }
    }

    private fun friendRequestAccepted(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendRequestAccepted = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(senderUserId!!).child("requestAccepted")

        friendRequestAccepted.setValue("Accepted").addOnCompleteListener(this) {
            if (it.isSuccessful){
                Toast.makeText(this, "Friend Added Successfully", Toast.LENGTH_SHORT).show()
                val acceptedRequest = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(senderUserId!!).child("requestAccepted")
                acceptedRequest.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val accepted = snapshot.getValue(String::class.java).toString()
                        if (accepted == "Accepted"){

                            databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(senderUserId!!)
                            databaseReference.removeValue()
                        }
                    }
                })
                val intentFriendRequest = Intent(this, FriendsActivity::class.java)
                intentFriendRequest.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentFriendRequest)
                finish()
            }
        }
    }

    private fun declineFriendList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(senderUserId!!)
        databaseReference.removeValue()

        val intentFriendRequest = Intent(this, FriendRequestActivity::class.java)
        intentFriendRequest.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentFriendRequest)
        finish()
    }
}