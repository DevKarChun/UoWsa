package com.example.uowsa.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView


class OtherUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var addFriendBtn: Button? = null
    private var cancelFriendRequestBtn: Button? = null
    private var otherUserId: String? = null
    private var userName: String? = null
    private var profileImage: String? = null
    private var status: String? = null
    private var optional1: TextView? = null
    private var optional2: TextView? = null
    private var studyCourse: String? = null
    private var campusLocation: String? = null
    private var acceptFriendBtn: Button? = null
    private var declineFriendRequestBtn: Button? = null
    private var blockedBtn: Button? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        auth = Firebase.auth

        val txtUsername = findViewById<TextView>(R.id.userName)
        val txtStatus = findViewById<TextView>(R.id.status)
        val userImage = findViewById<CircleImageView>(R.id.profileImage)
        addFriendBtn = findViewById(R.id.addFriendBtn)
        cancelFriendRequestBtn = findViewById(R.id.cancelFriendRequestBtn)
        acceptFriendBtn = findViewById(R.id.acceptFriendBtn)
        declineFriendRequestBtn = findViewById(R.id.declineFriendRequestBtn)
        blockedBtn = findViewById(R.id.BlockedBtn)
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

        otherUserId = intent.getStringExtra("userId").toString()
        userName = intent.getStringExtra("userName").toString()
        profileImage = intent.getStringExtra("profileImage").toString()
        status = intent.getStringExtra("status").toString()
        studyCourse = intent.getStringExtra("studyCourse").toString()
        campusLocation = intent.getStringExtra("campusLocation").toString()

        if (studyCourse == ""){
            optional1?.setText(campusLocation)
            optional1?.visibility = View.VISIBLE
        }else{
            optional1?.setText(studyCourse)
            optional2?.setText(campusLocation)
            optional1?.visibility = View.VISIBLE
            optional2?.visibility = View.VISIBLE
        }

        Log.d("tag", otherUserId!!)
        Log.d("tag", userName!!)
        Log.d("tag", profileImage!!)
        Log.d("tag", status!!)

        txtUsername.setText(userName)
        txtStatus.setText(status)
        Glide.with(this).load(profileImage).into(userImage)

        checkBlocked()

        addFriendBtn?.setOnClickListener {
            addFriendRequestDatabase()
        }

        cancelFriendRequestBtn?.setOnClickListener {
            cancelFriendRequestDatabase()
        }

        acceptFriendBtn?.setOnClickListener {
            addToFriendList()
        }

        declineFriendRequestBtn?.setOnClickListener {
            declineFriendList()
        }

    }

    private fun checkBlocked() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val databaseUser = FirebaseDatabase.getInstance().getReference("BlockedList").child(userId).child(otherUserId!!)
        databaseUser.child("blocked").get().addOnSuccessListener {
            val blocked = it.value.toString()
            if (blocked == "Blocked"){
                blockedBtn!!.visibility = View.VISIBLE
            }else{
                val databaseFriend = FirebaseDatabase.getInstance().getReference("BlockedList").child(otherUserId!!).child(userId)
                databaseFriend.child("blocked").get().addOnSuccessListener {
                    val friendBlocked = it.value.toString()
                    if (friendBlocked == "Blocked"){
                        blockedBtn!!.visibility = View.VISIBLE
                    }else{
                        friendAcceptDeclineRequest()
                    }
                }
            }
        }
    }

    private fun friendAcceptDeclineRequest(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendAcceptDeclineRequest = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!).child("senderUserId")

        friendAcceptDeclineRequest.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userFriendsId = snapshot.getValue(String::class.java).toString()
                Log.d("tag33496496749", userFriendsId)
                if (userFriendsId == otherUserId!!){
                    acceptFriendBtn?.visibility = View.VISIBLE
                    acceptFriendBtn?.isClickable = true
                    declineFriendRequestBtn?.visibility = View.VISIBLE
                    declineFriendRequestBtn?.isClickable = true
                }else{
                    friendExist()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun friendExist(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendExist = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(otherUserId!!).child("friends")
        Log.d("tagOtherUserId", otherUserId!!.toString())
        Log.d("tagFriendPath", friendExist.toString())

        friendExist.get().addOnSuccessListener { it ->
            val ifFriend = it.value.toString()
            Log.d("tagIfFriend", ifFriend)
            if (ifFriend == "Friend"){
                addFriendBtn?.setText("We are friends")
                addFriendBtn?.isClickable = false
                addFriendBtn?.visibility = View.VISIBLE
            }else{
                friendRequestSent()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun friendRequestSent(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendRequestSent = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)

        friendRequestSent.child("friends").get().addOnSuccessListener {
            val exist = it.value.toString()
            if (exist == "Friend"){
                addFriendBtn?.setText("Pending Friend Request")
                addFriendBtn?.isClickable = false
                addFriendBtn?.visibility = View.VISIBLE
                addFriendBtn?.setBackgroundColor(resources.getColor(R.color.greyL))
                cancelFriendRequestBtn?.visibility = View.VISIBLE
            }else{
                showAddFriendBtn()
            }
        }

//        friendRequestSent.addListenerForSingleValueEvent(object : ValueEventListener{
//            @SuppressLint("SetTextI18n")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userFriendsRequestId = snapshot.getValue(String::class.java).toString()
//                Log.d("tag333444", userFriendsRequestId)
//                if (userFriendsRequestId == otherUserId!!){
//                    addFriendBtn?.setText("Pending Friend Request")
//                    addFriendBtn?.isClickable = false
//                    addFriendBtn?.visibility = View.VISIBLE
//                    addFriendBtn?.setBackgroundColor(resources.getColor(R.color.greyL))
//                    cancelFriendRequestBtn?.visibility = View.VISIBLE
//                }else {
//                    showAddFriendBtn()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
    }

    @SuppressLint("SetTextI18n")
    private fun showAddFriendBtn(){
        //https://stackoverflow.com/questions/17277618/get-color-value-programmatically-when-its-a-reference-theme
        @ColorInt
        fun Context.getColorFromAttr(
            @AttrRes attrColor: Int,
            typedValue: TypedValue = TypedValue(),
            resolveRefs: Boolean = true
        ): Int {
            theme.resolveAttribute(attrColor, typedValue, resolveRefs)
            return typedValue.data
        }
        addFriendBtn?.setText("Add Friend")
        addFriendBtn?.isClickable = true
        addFriendBtn?.setBackgroundColor(getColorFromAttr(androidx.appcompat.R.attr.colorControlHighlight))
        addFriendBtn?.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun addFriendBtnDisable(){
        addFriendBtn?.setText("Pending Friend Request")
        addFriendBtn?.isClickable = false
        addFriendBtn?.setBackgroundColor(resources.getColor(R.color.pendingLightMode))
        cancelFriendRequestBtn?.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun addFriendBtnEnable(){
        //https://stackoverflow.com/questions/17277618/get-color-value-programmatically-when-its-a-reference-theme
        @ColorInt
        fun Context.getColorFromAttr(
            @AttrRes attrColor: Int,
            typedValue: TypedValue = TypedValue(),
            resolveRefs: Boolean = true
        ): Int {
            theme.resolveAttribute(attrColor, typedValue, resolveRefs)
            return typedValue.data
        }
        addFriendBtn?.setText("Add Friend")
        addFriendBtn?.isClickable = true
        addFriendBtn?.setBackgroundColor(getColorFromAttr(androidx.appcompat.R.attr.colorControlHighlight))
        cancelFriendRequestBtn?.visibility = View.INVISIBLE
    }

    private fun addFriendRequestDatabase(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val friendRequestData = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)

        friendRequestData.child("senderUserId").setValue(userId)
        friendRequestData.child("requestAccepted").setValue("notAccepted")
        friendRequestData.child("receiverFriendUserId").setValue(otherUserId!!)

//        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName").get().addOnSuccessListener {
//            Log.i("firebase1", "Got value ${it.value}")
//            val userNameInfo = it.value.toString()
//            val usernameData = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)
//            usernameData.addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.ref.child("senderUsername").setValue(userNameInfo)
//                }
//            })
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }
//
//        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profileImage").get().addOnSuccessListener {
//            Log.i("firebase2", "Got value ${it.value}")
//            val imageInfo = it.value
//            val imageData = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)
//            imageData.addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.ref.child("senderProfileImage").setValue(imageInfo)
//                }
//            })
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }
//
//        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status").get().addOnSuccessListener {
//            Log.i("firebase3", "Got value ${it.value}")
//            val statusInfo = it.value
//            val statusData = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)
//            statusData.addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.ref.child("senderStatus").setValue(statusInfo)
//                }
//            })
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }

        addFriendBtnDisable()

    }

    private fun cancelFriendRequestDatabase(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(otherUserId!!).child(userId)
        Log.d("tag", databaseReference.toString())
        databaseReference.removeValue().addOnCompleteListener(this) {
            if (it.isSuccessful){
                addFriendBtnEnable()
                Toast.makeText(this, "The friend request has been removed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Error: Cancel Friend Request", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToFriendList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!).child("senderUserId").get().addOnSuccessListener {
            val senderId = it.value

            if(senderId == otherUserId!!){
                val addFriendToUserFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(otherUserId!!)

                addFriendToUserFriendList.child("friendUserId").setValue(otherUserId!!)
//                addFriendToUserFriendList.child("friendUserName").setValue(userName!!)
//                addFriendToUserFriendList.child("friendStatus").setValue(status!!)
//                addFriendToUserFriendList.child("friendProfileImage").setValue(profileImage!!)
                addFriendToUserFriendList.child("friends").setValue("Friend")

                val addFriendToOtherFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(otherUserId!!).child(userId)

                addFriendToOtherFriendList.child("friendUserId").setValue(userId)

//                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName").get().addOnSuccessListener {
//                    Log.i("firebase3", "Got value ${it.value}")
//                    val userNameInfo = it.value
//                    val userNameData = FirebaseDatabase.getInstance().getReference("FriendsList").child(otherUserId!!).child(userId)
//                    userNameData.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.ref.child("friendUserId").setValue(userNameInfo)
//                        }
//                    })
//                }.addOnFailureListener{
//                    Log.e("firebase", "Error getting data", it)
//                }

//                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("status").get().addOnSuccessListener {
//                    Log.i("firebase3", "Got value ${it.value}")
//                    val statusInfo = it.value
//                    val statusData = FirebaseDatabase.getInstance().getReference("FriendsList").child(otherUserId!!).child(userId)
//                    statusData.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.ref.child("friendStatus").setValue(statusInfo)
//                        }
//                    })
//                }.addOnFailureListener{
//                    Log.e("firebase", "Error getting data", it)
//                }
//
//                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profileImage").get().addOnSuccessListener {
//                    Log.i("firebase3", "Got value ${it.value}")
//                    val imageInfo = it.value
//                    val imageData = FirebaseDatabase.getInstance().getReference("FriendsList").child(otherUserId!!).child(userId)
//                    imageData.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            snapshot.ref.child("friendProfileImage").setValue(imageInfo)
//                        }
//                    })
//                }.addOnFailureListener{
//                    Log.e("firebase", "Error getting data", it)
//                }

                addFriendToOtherFriendList.child("friends").setValue("Friend")

                friendRequestAccepted()

            }

        }

    }

    private fun friendRequestAccepted(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendRequestAccepted = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!).child("requestAccepted")

        friendRequestAccepted.setValue("Accepted").addOnCompleteListener(this) {
            if (it.isSuccessful){
                Toast.makeText(this, "Friend Added Successfully", Toast.LENGTH_SHORT).show()
                val acceptedRequest = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!).child("requestAccepted")
                acceptedRequest.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val accepted = snapshot.getValue(String::class.java).toString()
                        if (accepted == "Accepted"){

                            databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!)
                            databaseReference.removeValue()
                        }
                    }
                })
                val intentFriendsActivity = Intent(this, FriendsActivity::class.java)
                intentFriendsActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentFriendsActivity)
                finish()
            }
        }
    }

    private fun declineFriendList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(otherUserId!!)
        databaseReference.removeValue()

        val intentFriendRequest = Intent(this, SearchFriendsActivity::class.java)
        intentFriendRequest.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentFriendRequest)
        finish()
    }
}