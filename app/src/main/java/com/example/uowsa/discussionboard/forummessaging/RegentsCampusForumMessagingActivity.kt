package com.example.uowsa.discussionboard.forummessaging

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.adapter.forumadapter.RegentsCampusMessageAdapter
import com.example.uowsa.usermodel.forummessagingmodel.RegentsCampusMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RegentsCampusForumMessagingActivity : AppCompatActivity() {

    private var regentsCampusMessageList = ArrayList<RegentsCampusMessage>()
    private var scrollDown: ScrollView? = null
    private lateinit var auth: FirebaseAuth
    private var regentsCampusForumMessagingRecyclerView: RecyclerView? = null
    private var regentsCampusCreatorId: String? = null
    private var regentsCampusCreatedDateTime : String? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regents_campus_forum_messaging)

        auth = Firebase.auth

        scrollDown = findViewById(R.id.scrollDown)
        regentsCampusForumMessagingRecyclerView = findViewById(R.id.regentsCampusForumMessagingRecyclerView)
        val deleteAllMessagesBtn = findViewById<ImageButton>(R.id.deleteAllMessagesBtn)

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

        regentsCampusCreatorId = intent.getStringExtra("creatorId").toString()
        val studyTitle = intent.getStringExtra("studyTitle").toString()
        val studyDescription = intent.getStringExtra("studyDescription").toString()
        val studyDate = intent.getStringExtra("studyDate").toString()
        regentsCampusCreatedDateTime = intent.getStringExtra("createdDateTime").toString()

        val postedUserImage = findViewById<CircleImageView>(R.id.circleImageView)
        val title = findViewById<TextView>(R.id.title)
        val description = findViewById<TextView>(R.id.description)
        val date = findViewById<TextView>(R.id.date)
        val postedBy = findViewById<TextView>(R.id.postedBy)

        title.setText(studyTitle)
        description.setText(studyDescription)
        date.setText(studyDate)

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(regentsCampusCreatorId!!)
        databaseRef.child("userName").get().addOnSuccessListener {
            val name = it.value.toString()
            postedBy.setText(name)
        }
        databaseRef.child("profileImage").get().addOnSuccessListener {
            val image = it.value.toString()
            Glide.with(this).load(image).into(postedUserImage)
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        regentsCampusForumMessagingRecyclerView?.layoutManager = layoutManager
        regentsCampusForumMessagingRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        readMessage()

        deleteAllMessagesBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTheme)
            val dialog: AlertDialog = builder.setTitle("Delete All Your Messages")
                .setMessage("Are you sure you want to delete all of your messages?")
                .setPositiveButton("Cancel") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("Delete") { dialog, _ ->
                    deleteAllMessages()
                    dialog.dismiss()
                }
                .create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        val messageTxt = findViewById<EditText>(R.id.messageTxt).text
        val clearMessage = findViewById<EditText>(R.id.messageTxt)
        val sendBtn = findViewById<ImageButton>(R.id.sendMessage)

        sendBtn.setOnClickListener {
            Log.d("tagtext", messageTxt.toString())
            val message = messageTxt.toString()
            Log.d("tagtext1", message)
            if(messageTxt.isEmpty()){
                Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(message)
                clearMessage.text.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val sendMessageDatabase = FirebaseDatabase.getInstance().getReference("RegentsCampusMessage").child(regentsCampusCreatorId!!).child(regentsCampusCreatedDateTime!!)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.now().format(formatter)
        Log.d("tagdatetime", dateTime.toString())

        val messagingContent: HashMap <String, String> = HashMap()
        messagingContent["senderRegentsCampusId"] = userId
        messagingContent["regentsCampusMessage"] = message
        messagingContent["regentsCampusMessageDateTime"] = dateTime
        messagingContent["regentsCampusCreatorId"] = regentsCampusCreatorId!!
        messagingContent["regentsCampusCreatedDateTime"] = regentsCampusCreatedDateTime!!

        sendMessageDatabase.push().setValue(messagingContent)
    }

    private fun deleteAllMessages() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid

        val userRef = FirebaseDatabase.getInstance().getReference("RegentsCampusMessage").child(regentsCampusCreatorId!!).child(regentsCampusCreatedDateTime!!)

        val userMessageDelete = userRef.orderByChild("senderRegentsCampusId").equalTo(userId)
        userMessageDelete.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot01: DataSnapshot in snapshot.children){
                    dataSnapshot01.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun readMessage() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val readMessageDatabase = FirebaseDatabase.getInstance().getReference("RegentsCampusMessage").child(regentsCampusCreatorId!!).child(regentsCampusCreatedDateTime!!)
        Log.d("tagDatabase", readMessageDatabase.toString())

        readMessageDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                regentsCampusMessageList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(RegentsCampusMessage::class.java)
                    Log.d("tagChat", chat.toString())
                    Log.d("tagUserId", userId)
                    Log.d("tagsenderId", chat!!.senderRegentsCampusId)
                    Log.d("tagfriendId", chat.regentsCampusMessage)
                    Log.d("tagmessagetime", chat.regentsCampusCreatedDateTime)

                    regentsCampusMessageList.add(chat)
                    scrollDown!!.scrollTo(0, scrollDown!!.bottom)
                }
                val regentsCampusMessageAdapter = RegentsCampusMessageAdapter(this@RegentsCampusForumMessagingActivity, regentsCampusMessageList)

                regentsCampusForumMessagingRecyclerView?.adapter = regentsCampusMessageAdapter
                regentsCampusForumMessagingRecyclerView?.scrollToPosition(regentsCampusMessageList.size-1)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}