package com.example.uowsa.activity

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
import com.example.uowsa.usermodel.generalmodel.AdminMessage
import com.example.uowsa.adapter.generaladapter.AdminMessageAdapter
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

class AdminMessagingActivity : AppCompatActivity() {

    var adminMessageList = ArrayList<AdminMessage>()
    private var adminUserId: String? = null
    private var adminMessageRecyclerView: RecyclerView? = null
    private var scrollDown: ScrollView? = null
    private lateinit var auth: FirebaseAuth

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_messaging)

        auth = Firebase.auth

        val friendImage = findViewById<CircleImageView>(R.id.adminUserImage)
        val deleteAllMessagesBtn = findViewById<ImageButton>(R.id.deleteAllMessagesBtn)

        adminMessageRecyclerView = findViewById(R.id.adminMessageRecyclerView)
        scrollDown = findViewById(R.id.scrollDown)


        val messageTxt = findViewById<EditText>(R.id.messageTxt).text
        val clearMessage = findViewById<EditText>(R.id.messageTxt)
        val sendBtn = findViewById<ImageButton>(R.id.sendMessage)

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

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        adminMessageRecyclerView?.layoutManager = layoutManager
        adminMessageRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adminUserId = intent.getStringExtra("userId").toString()

//        intent.putExtra("userId",user.userId)
//        intent.putExtra("userName",user.userName)
//        intent.putExtra("profileImage", user.profileImage)
//        intent.putExtra("status", user.status)
//        intent.putExtra("studyCourse", user.studyCourse)
//        intent.putExtra("campusLocation", user.campusLocation)

        readMessage()

        deleteAllMessagesBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTheme)
            val dialog: AlertDialog = builder.setTitle("Delete All Your Messages")
                .setMessage("Are you sure you want to delete all of your messages?")
                .setPositiveButton("Cancel") { dialog, _ ->
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

        val databaseUserInfo = FirebaseDatabase.getInstance().getReference("Users").child(adminUserId!!)
        databaseUserInfo.child("profileImage").get().addOnSuccessListener {
            val imageGet = it.value.toString()
            Glide.with(this).load(imageGet).into(friendImage)
        }

        adminMessageRecyclerView?.scrollToPosition(adminMessageList.size-1)

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

    private fun deleteAllMessages() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid

        val userRef = FirebaseDatabase.getInstance().getReference("contactAdmin").child(userId).child(adminUserId!!)
        val friendRef = FirebaseDatabase.getInstance().getReference("contactAdmin").child(adminUserId!!).child(userId)

        val userMessageDelete = userRef.orderByChild("senderAdminMessageId").equalTo(userId)
        userMessageDelete.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot01: DataSnapshot in snapshot.children){
                    dataSnapshot01.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val userFriendNodeMessageDelete = friendRef.orderByChild("senderAdminMessageId").equalTo(userId)
        userFriendNodeMessageDelete.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot02: DataSnapshot in snapshot.children){
                    dataSnapshot02.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun readMessage() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val adminUserId = adminUserId.toString()
        val readMessageDatabase = FirebaseDatabase.getInstance().getReference("contactAdmin").child(userId).child(adminUserId)
        Log.d("tagDatabase", readMessageDatabase.toString())

        readMessageDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminMessageList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(AdminMessage::class.java)
                    Log.d("tagChat", chat.toString())
                    Log.d("tagUserId", userId)
                    Log.d("tagsenderId", chat!!.senderAdminMessageId)
                    Log.d("tagfriendId", adminUserId)
                    Log.d("tagretrievedId", chat.receiverAdminMessageId)

                    if (((chat.senderAdminMessageId == userId) && (chat.receiverAdminMessageId == adminUserId)) ||
                        ((chat.senderAdminMessageId == adminUserId) && (chat.receiverAdminMessageId == userId)))
                    {
                        adminMessageList.add(chat)
                        scrollDown!!.scrollTo(0, scrollDown!!.bottom)
                    }
                }
                Log.d("taguowmessageList", adminMessageList.toString())
                val adminMessageAdapter = AdminMessageAdapter(this@AdminMessagingActivity, adminMessageList)

                adminMessageRecyclerView?.adapter = adminMessageAdapter
                Log.d("tag22", adminMessageRecyclerView.toString())
                adminMessageRecyclerView?.scrollToPosition(adminMessageList.size-1)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val resetUnreadMessage = FirebaseDatabase.getInstance().getReference("contactAdmin").child(userId).child(adminUserId)

        val unreadCounter = resetUnreadMessage.orderByChild("messageStatus").equalTo("unread")
        unreadCounter.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{it->
                    val unreadKey = it.key.toString()
                    Log.d("tag232", unreadKey)
                    resetUnreadMessage.child(unreadKey).child("messageStatus").setValue("read")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun sendMessage(message: String) {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val sendMessageDatabase = FirebaseDatabase.getInstance().getReference("contactAdmin").child(userId).child(adminUserId!!)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.now().format(formatter)
        Log.d("tagdatetime", dateTime.toString())

        val messagingContent: HashMap <String, String> = HashMap()
        messagingContent["senderAdminMessageId"] = userId
        messagingContent["receiverAdminMessageId"] = adminUserId!!
        messagingContent["message"] = message
        messagingContent["messageDateTime"] = dateTime
        messagingContent["messageStatus"] = "read"


        sendMessageDatabase.push().setValue(messagingContent)

        val sendMessageDatabaseFriendUser = FirebaseDatabase.getInstance().getReference("contactAdmin").child(adminUserId!!).child(userId)

        val messagingContentFriendUser: HashMap <String, String> = HashMap()
        messagingContentFriendUser["senderAdminMessageId"] = userId
        messagingContentFriendUser["receiverAdminMessageId"] = adminUserId!!
        messagingContentFriendUser["message"] = message
        messagingContentFriendUser["messageDateTime"] = dateTime
        messagingContentFriendUser["messageStatus"] = "unread"

        sendMessageDatabaseFriendUser.push().setValue(messagingContentFriendUser)

    }
}