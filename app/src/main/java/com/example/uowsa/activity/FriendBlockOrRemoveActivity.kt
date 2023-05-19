package com.example.uowsa.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class FriendBlockOrRemoveActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var friendUserId: String? = null
    private var optional1: TextView? = null
    private var optional2: TextView? = null
    private var studyCourse: String? = null
    private var campusLocation: String? = null

    private var friends: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_block_or_remove)

        auth = Firebase.auth

        val removeFriendBtn = findViewById<Button>(R.id.removeFriendBtn)
        val blockFriendBtn = findViewById<Button>(R.id.blockFriendBtn)
        val username = findViewById<TextView>(R.id.userName)
        val status = findViewById<TextView>(R.id.status)
        val profileImage = findViewById<CircleImageView>(R.id.userImage)
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

        friendUserId = intent.getStringExtra("friendUserId").toString()
        friends = intent.getStringExtra("friends").toString()

        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(friendUserId!!)
        databaseRefresh.child("userName").get().addOnSuccessListener {
            val usernameRefresh = it.value.toString()
            username.setText(usernameRefresh)
        }
        databaseRefresh.child("status").get().addOnSuccessListener {
            val statusRefresh = it.value.toString()
            status.setText(statusRefresh)
        }
        databaseRefresh.child("profileImage").get().addOnSuccessListener {
            val imageRefresh = it.value.toString()
            Glide.with(this).load(imageRefresh).into(profileImage)
        }

        val acceptFriendOptionals = FirebaseDatabase.getInstance().getReference("Users").child(friendUserId!!)
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
        optional1?.visibility = View.VISIBLE
        optional2?.visibility = View.VISIBLE

        removeFriendBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTheme)
            databaseRefresh.child("userName").get().addOnSuccessListener{
                val usernameRefresh = it.value.toString()
                val dialog: AlertDialog = builder.setTitle("Remove Friend")
                    .setMessage("Are you sure you want to remove $usernameRefresh from your friends?")
                    .setPositiveButton("Cancel") {
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Remove") { dialog, _ ->
                        removeFriend()
                        dialog.dismiss()
                        val friendsIntent = Intent(this, FriendsActivity::class.java)
                        friendsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(friendsIntent)
                        finish()
                    }
                    .create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        blockFriendBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTheme)
            databaseRefresh.child("userName").get().addOnSuccessListener{
                val usernameRefresh = it.value.toString()
                val dialog: AlertDialog = builder.setTitle("Block Friend")
                    .setMessage("Are you sure you want to block $usernameRefresh?")
                    .setPositiveButton("Cancel") {
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Block") { dialog, _ ->
                        blockFriend()
                        dialog.dismiss()
                        val friendsIntent = Intent(this, FriendsActivity::class.java)
                        friendsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(friendsIntent)
                        finish()
                    }
                    .create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

    }

    private fun blockFriend() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid


        val databaseFirebase2 = FirebaseDatabase.getInstance().getReference("BlockedList").child(userId).child(friendUserId!!)

        databaseFirebase2.child("userBlockedUserId").setValue(friendUserId)

        val databaseFirebase = FirebaseDatabase.getInstance().getReference("FriendsList")
        val userSingleDelete = databaseFirebase.child(userId).child(friendUserId!!)
        val friendSingleDelete = databaseFirebase.child(friendUserId!!).child(userId)

        userSingleDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        friendSingleDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun removeFriend() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val databaseFirebase = FirebaseDatabase.getInstance().getReference("FriendsList")
        val userSingleDelete = databaseFirebase.child(userId).child(friendUserId!!)
        val friendSingleDelete = databaseFirebase.child(friendUserId!!).child(userId)

        userSingleDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        friendSingleDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val databaseFirebase1 = FirebaseDatabase.getInstance().getReference("UoWMessage")
        val userFriendMessagesDelete = databaseFirebase1.child(userId).child(friendUserId!!)
        val friendMessagesDelete = databaseFirebase1.child(friendUserId!!).child(userId)

        userFriendMessagesDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        friendMessagesDelete.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}