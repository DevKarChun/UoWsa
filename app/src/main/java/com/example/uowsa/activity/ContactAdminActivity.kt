package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.adapter.generaladapter.AdminAdapter
import com.example.uowsa.usermodel.generalmodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ContactAdminActivity : AppCompatActivity() {

    var adminList = ArrayList<User>()
    private var contactAdminRecyclerView: RecyclerView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_admin)

        auth = Firebase.auth

        contactAdminRecyclerView = findViewById(R.id.contactAdminRecyclerView)
        contactAdminRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val contactAdmin = findViewById<TextView>(R.id.contactAdmin)

        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val userPath = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName")
        userPath.get().addOnSuccessListener {
            val name = it.value.toString()
            val nameLower = name.lowercase()
            Log.d("tag", name)
            Log.d("taglower", nameLower)
            if (nameLower == "admin"){
                contactAdmin.setText("Contact Users")
                contactAdmin.visibility = View.VISIBLE
            }else{
                contactAdmin.setText("Contact Admin")
                contactAdmin.visibility = View.VISIBLE
            }
        }

        verifyUser()
//            nameData.addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val name = snapshot.getValue().toString().lowercase()
//                    Log.d("tagadminname", name)
//                    if (name == "admin"){
//                        getUserList()
//                    }else{
//                        getAdminList()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })


//        nameData.get().addOnSuccessListener {
//            val name = it.value.toString()
//            Log.d("tagAdminName", name)
//            if (name == "admin"){
//                Log.d("tagAdminName", name)
//            }
//        }

//        getAdminList()

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
    }

    private fun verifyUser() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val databaseName = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userName")
        databaseName.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.value.toString()
                val nameLower = name.lowercase()
                if (nameLower == "admin"){
                    getUserList()
                }else{
                    getAdminList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getUserList(){
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val adminUser = FirebaseDatabase.getInstance().getReference("Users")
        adminUser.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val users = dataSnapShot.getValue(User::class.java)
                    if (users!!.userId != userId) {
                        adminList.add(users)
                    }
                }

                val adminAdapter = AdminAdapter(this@ContactAdminActivity, adminList)

                contactAdminRecyclerView?.adapter = adminAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getAdminList() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val users = dataSnapShot.getValue(User::class.java)
                    val admin = "admin"
                    val adminCheck = users!!.userName.lowercase()
                    if (users.userId != userId && adminCheck == admin) {
                        adminList.add(users)
                    }
                }

                val adminAdapter = AdminAdapter(this@ContactAdminActivity, adminList)

                contactAdminRecyclerView?.adapter = adminAdapter
            }

        })
    }
}