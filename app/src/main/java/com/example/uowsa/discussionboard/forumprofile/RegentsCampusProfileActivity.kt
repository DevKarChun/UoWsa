package com.example.uowsa.discussionboard.forumprofile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.activity.*
import com.example.uowsa.discussionboard.createnewforum.StudyNewForumActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class RegentsCampusProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var addFriendBtn: Button? = null
    private var cancelFriendRequestBtn: Button? = null
    private var friendUserId: String? = null
    private var acceptFriendBtn: Button? = null
    private var declineFriendRequestBtn: Button? = null
    private var blockedBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regents_campus_profile)

        auth = Firebase.auth

        val txtUsername = findViewById<TextView>(R.id.userName)
        val txtStatus = findViewById<TextView>(R.id.status)
        val userImage = findViewById<CircleImageView>(R.id.profileImage)
        val optional1 = findViewById<TextView>(R.id.optional1)
        val optional2 = findViewById<TextView>(R.id.optional2)
        addFriendBtn = findViewById(R.id.addFriendBtn)
        cancelFriendRequestBtn = findViewById(R.id.cancelFriendRequestBtn)
        acceptFriendBtn = findViewById(R.id.acceptFriendBtn)
        declineFriendRequestBtn = findViewById(R.id.declineFriendRequestBtn)
        blockedBtn = findViewById(R.id.BlockedBtn)

        val homeBtn = findViewById<ImageButton>(R.id.navHomeBtn)
        val profileBtn = findViewById<ImageButton>(R.id.navProfileBtn)
        val uowMessageBtn = findViewById<ImageButton>(R.id.navUowMessage)
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)

        homeBtn.setOnClickListener {
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

        friendUserId = intent.getStringExtra("friendUserId").toString()

        val databaseProfile = FirebaseDatabase.getInstance().getReference("Users").child(friendUserId!!)
        databaseProfile.child("userName").get().addOnSuccessListener {
            val usernameRefresh = it.value.toString()
            txtUsername.setText(usernameRefresh)
        }
        databaseProfile.child("status").get().addOnSuccessListener {
            val statusRefresh = it.value.toString()
            txtStatus.setText(statusRefresh)
        }
        databaseProfile.child("profileImage").get().addOnSuccessListener {
            val imageRefresh = it.value.toString()
            Glide.with(this).load(imageRefresh).into(userImage)
        }
        databaseProfile.child("studyCourse").get().addOnSuccessListener {
            val studyCourse = it.value.toString()
            Log.d("tag", studyCourse)
            optional1.setText(studyCourse)
            optional1.visibility = View.VISIBLE
        }
        databaseProfile.child("campusLocation").get().addOnSuccessListener {
            val campusLocation = it.value.toString()
            if (optional1.text.toString().isEmpty()){
                optional1.setText(campusLocation)
                optional1.visibility = View.VISIBLE
            }else{
                optional2.setText(campusLocation)
                optional2.visibility = View.VISIBLE
            }
        }

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
        val databaseUser = FirebaseDatabase.getInstance().getReference("BlockedList").child(userId).child(friendUserId!!)
        databaseUser.child("blocked").get().addOnSuccessListener {
            val blocked = it.value.toString()
            if (blocked == "Blocked"){
                blockedBtn!!.visibility = View.VISIBLE
            }else{
                val databaseFriend = FirebaseDatabase.getInstance().getReference("BlockedList").child(friendUserId!!).child(userId)
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
        val friendAcceptDeclineRequest = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!).child("senderUserId")

        friendAcceptDeclineRequest.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userFriendsId = snapshot.getValue(String::class.java).toString()
                Log.d("tag33496496749", userFriendsId)
                if (userFriendsId == friendUserId!!){
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
        val friendExist = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(friendUserId!!).child("friends")
        Log.d("tagOtherUserId", friendUserId!!.toString())
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
        val friendRequestSent = FirebaseDatabase.getInstance().getReference("FriendRequest").child(friendUserId!!).child(userId)

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
        addFriendBtn?.setBackgroundColor(resources.getColor(R.color.greyL))
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

        val friendRequestData = FirebaseDatabase.getInstance().getReference("FriendRequest").child(friendUserId!!).child(userId)

        friendRequestData.child("senderUserId").setValue(userId)
        friendRequestData.child("requestAccepted").setValue("notAccepted")
        friendRequestData.child("receiverFriendUserId").setValue(friendUserId!!)

        addFriendBtnDisable()

    }

    private fun cancelFriendRequestDatabase(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(friendUserId!!).child(userId)
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

        FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!).child("senderUserId").get().addOnSuccessListener {
            val senderId = it.value

            if(senderId == friendUserId!!){
                val addFriendToUserFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(friendUserId!!)

                addFriendToUserFriendList.child("friendUserId").setValue(friendUserId!!)
                addFriendToUserFriendList.child("friends").setValue("Friend")

                val addFriendToOtherFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(friendUserId!!).child(userId)

                addFriendToOtherFriendList.child("friendUserId").setValue(userId)
                addFriendToOtherFriendList.child("friends").setValue("Friend")

                friendRequestAccepted()
            }
        }
    }

    private fun friendRequestAccepted(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val friendRequestAccepted = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!).child("requestAccepted")

        friendRequestAccepted.setValue("Accepted").addOnCompleteListener(this) {
            if (it.isSuccessful){
                Toast.makeText(this, "Friend Added Successfully", Toast.LENGTH_SHORT).show()
                val acceptedRequest = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!).child("requestAccepted")
                acceptedRequest.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val accepted = snapshot.getValue(String::class.java).toString()
                        if (accepted == "Accepted"){

                            databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!)
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

        databaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(userId).child(friendUserId!!)
        databaseReference.removeValue()

        val intentFriendRequest = Intent(this, StudyNewForumActivity::class.java)
        intentFriendRequest.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentFriendRequest)
        finish()
    }
}