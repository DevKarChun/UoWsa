package com.example.uowsa.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var usernameView: TextView? = null
    var statusMessageView: TextView? = null
    var optional1: TextView? = null
    var optional2: TextView? = null
    private var profileImage: ImageView? = null
    private var uploadBtn: ImageButton? = null
    lateinit var uploadImageUri: Uri
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth

        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val connectivityStatus = findViewById<TextView>(R.id.connectivityStatus)
        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        databaseRefresh.child("connectivityStatus").get().addOnSuccessListener {
            val connectivityStatusInfo = it.value.toString()
            if (connectivityStatusInfo == "Online"){
                connectivityStatus.setBackgroundResource(R.drawable.connectivity_status_online)
            }else{
                connectivityStatus.setBackgroundResource(R.drawable.connectivity_status_offline)
            }
        }

        profileImage = findViewById(R.id.profileImage)
        val editImageBtn = findViewById<ImageButton>(R.id.editImageBtn)
        usernameView = findViewById(R.id.namePlaceholder)
        statusMessageView = findViewById(R.id.statusMessage)
        uploadBtn = findViewById(R.id.uploadBtn)
        optional1 = findViewById(R.id.optional1)
        optional2 = findViewById(R.id.optional2)

        val navHome = findViewById<ImageButton>(R.id.navHomeBtn)
        val navProfile = findViewById<ImageButton>(R.id.navProfileBtn)
        val navMessage = findViewById<ImageButton>(R.id.navUowMessage)
        val navSettings = findViewById<ImageButton>(R.id.navSettings)

        readImage()
        readUsername()
        readStatus()
        readOptionals()

        editImageBtn.setOnClickListener {
            selectImage()
        }

        uploadBtn?.setOnClickListener {
            clearDatabaseImage()
            uploadImage()
            if (uploadBtn?.isEnabled == true) {
                uploadBtn?.visibility = View.INVISIBLE
            }
        }

        navHome.setOnClickListener {
            val navIntentHome = Intent(this, HomeActivity::class.java)
            startActivity(navIntentHome)
        }

        navProfile.setOnClickListener {
            val navIntentProfile = Intent(this, ProfileActivity::class.java)
            startActivity(navIntentProfile)
        }

        navMessage.setOnClickListener {
            val navIntentUoWMessage = Intent(this, UoWMessageActivity::class.java)
            startActivity(navIntentUoWMessage)
        }

        navSettings.setOnClickListener {
            val navIntentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(navIntentSettings)
        }
    }

    private fun readImage() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val storageRef = FirebaseStorage.getInstance().getReference("images")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val databaseImage =
            FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profileImage")
                .get().addOnSuccessListener {
                    val imageExist = it.value.toString()
                    if (imageExist == "") {
                        profileImage?.setImageResource(R.mipmap.ic_launcher)
                    } else if (imageExist.contains(userId)) {
                        val file = File.createTempFile("tempFileImage", "jpg")
                        storageRef.getFile(file).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            profileImage?.setImageBitmap(bitmap)
                        }
                    } else {
                        val tempStorageRef = FirebaseStorage.getInstance().getReference("appImage")
                            .child("ic_launcher.png")
                        val file = File.createTempFile("tempFileImage", "jpg")
                        tempStorageRef.getFile(file).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            profileImage?.setImageBitmap(bitmap)
                        }
                    }
                }
    }

        private fun clearDatabaseImage() {
            val user = auth.currentUser?.uid.toString()
            val storageRef = FirebaseStorage.getInstance().getReference("images")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            //Delete image if there are any before uploading a new image.
            storageRef.delete().addOnSuccessListener {
                Toast.makeText(this@ProfileActivity, "Replacing Image...", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        private fun uploadImage() {
            val user = auth.currentUser?.uid.toString()
            Log.d("tag", user)
            val storageRef = FirebaseStorage.getInstance().getReference("images")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            val userId = auth.currentUser?.uid!!

            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            val uploadTask = storageRef.putFile(uploadImageUri)
            uploadTask.continueWith {
                storageRef.downloadUrl
            }.addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    it.result.addOnSuccessListener { task ->
//                    val hashMap:HashMap<String,String> = HashMap()
                        Log.d("tag", uploadImageUri.toString())
                        val url = task.toString()
//                    hashMap["profileImage"] = url
//                    Log.d("tag", url)
//                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                        databaseReference.child("profileImage").setValue(url)
                            .addOnCompleteListener(this) {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Image is successfully uploaded",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
            }

//        storageRef.putFile(uploadImageUri).addOnSuccessListener {
//            val hashMap:HashMap<String,String> = HashMap()
//            Log.d("tag", uploadImageUri.toString())
//            val url = storageRef.downloadUrl.result
//            hashMap["profileImage"] = url.toString()
//            Log.d("tag", url.toString())
//            databaseReference.updateChildren(hashMap as Map<String, Any>)
//            Toast.makeText(this@ProfileActivity, "Image uploaded successfully...", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener{
//            Toast.makeText(this@ProfileActivity, "Unable to upload image", Toast.LENGTH_SHORT).show()
//        }
        }

        private fun selectImage() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 100)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 100 && resultCode == RESULT_OK) {
                uploadImageUri = data?.data!!
                profileImage?.setImageURI(uploadImageUri)
                Log.d("tag", uploadImageUri.toString())
                uploadBtn?.visibility = View.VISIBLE
            }
        }

        private fun readUsername() {
            val user: FirebaseUser? = auth.currentUser
            val userId: String = user!!.uid
            Log.d("tag", userId)

            val databaseUsers = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("userName")
            Log.d("tag", databaseUsers.toString())

            databaseUsers.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.getValue(String::class.java).toString()
                    Log.d("tag", username)
                    usernameView?.setText(username)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error retrieving username...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        private fun readStatus() {
            val user: FirebaseUser? = auth.currentUser
            val userId: String = user!!.uid
            Log.d("tag", userId)

            val databaseStatus = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("status")
            Log.d("tag", databaseStatus.toString())

            databaseStatus.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java).toString()
                    Log.d("tag", status)
                    statusMessageView?.setText(status)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error retrieving user status...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        private fun readOptionals() {
            val user: FirebaseUser? = auth.currentUser
            val userId: String = user!!.uid
            Log.d("tag", userId)

            val databaseStudyCourse = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("studyCourse")
            Log.d("tag", databaseStudyCourse.toString())

            databaseStudyCourse.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val studyCourse = snapshot.getValue(String::class.java).toString()
                    Log.d("tag", studyCourse)
                    if (studyCourse == "") {
                        val databaseCampusLocation1 =
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child("campusLocation")
                        Log.d("tag", databaseCampusLocation1.toString())

                        databaseCampusLocation1.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val campusLocation =
                                    snapshot.getValue(String::class.java).toString()
                                Log.d("tag", campusLocation)
                                optional1?.setText(campusLocation)
                                optional1?.visibility = View.VISIBLE
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    } else {
                        optional1?.setText(studyCourse)
                        optional1?.visibility = View.VISIBLE
                        val databaseCampusLocation =
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child("campusLocation")
                        Log.d("tag", databaseCampusLocation.toString())

                        databaseCampusLocation.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val campusLocation =
                                    snapshot.getValue(String::class.java).toString()
                                Log.d("tag", campusLocation)
                                optional2?.setText(campusLocation)
                                optional2?.visibility = View.VISIBLE
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    }


