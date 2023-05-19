package com.example.uowsa.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.adapter.generaladapter.UoWMessageAdapter
import com.example.uowsa.usermodel.generalmodel.UoWMessage
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

class UoWMessagingActivity : AppCompatActivity() {

    var uoWMessageList = ArrayList<UoWMessage>()
    private var friendUserId: String? = null
    private var friends: String? = null
    private var messageRecyclerView: RecyclerView? = null
    private var scrollDown: ScrollView? = null
    private lateinit var auth: FirebaseAuth

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uo_wmessaging)

        auth = Firebase.auth

        val friendName = findViewById<TextView>(R.id.friendNameTitle)
        val friendImage = findViewById<CircleImageView>(R.id.friendUserImage)
        val deleteAllMessagesBtn = findViewById<ImageButton>(R.id.deleteAllMessagesBtn)

        messageRecyclerView = findViewById(R.id.messageRecyclerView)
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
        messageRecyclerView?.layoutManager = layoutManager
        messageRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        friends = intent.getStringExtra("friends").toString()
        friendUserId = intent.getStringExtra("friendUserId").toString()
        Log.d("tagFriendUserId", friendUserId!!)

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

        val databaseUserInfo = FirebaseDatabase.getInstance().getReference("Users").child(friendUserId!!)
        databaseUserInfo.child("userName").get().addOnSuccessListener {
            val usernameGet = it.value.toString()
            friendName.setText(usernameGet)
        }
        databaseUserInfo.child("profileImage").get().addOnSuccessListener {
            val imageGet = it.value.toString()
            Glide.with(this).load(imageGet).into(friendImage)
        }

        messageRecyclerView?.scrollToPosition(uoWMessageList.size-1)

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

        val userRef = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId).child(friendUserId!!)
        val friendRef = FirebaseDatabase.getInstance().getReference("UoWMessage").child(friendUserId!!).child(userId)

        val userMessageDelete = userRef.orderByChild("senderUoWMessageId").equalTo(userId)
        userMessageDelete.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot01: DataSnapshot in snapshot.children){
                    dataSnapshot01.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val userFriendNodeMessageDelete = friendRef.orderByChild("senderUoWMessageId").equalTo(userId)
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
        val friendUserId = friendUserId.toString()
        val readMessageDatabase = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId).child(friendUserId)
        Log.d("tagDatabase", readMessageDatabase.toString())

        readMessageDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uoWMessageList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(UoWMessage::class.java)
                    Log.d("tagChat", chat.toString())
                    Log.d("tagUserId", userId)
                    Log.d("tagsenderId", chat!!.senderUoWMessageId)
                    Log.d("tagfriendId", friendUserId)
                    Log.d("tagretrievedId", chat.receiverUoWMessageId)

                    if (((chat.senderUoWMessageId == userId) && (chat.receiverUoWMessageId == friendUserId)) ||
                        ((chat.senderUoWMessageId == friendUserId) && (chat.receiverUoWMessageId == userId)))
                    {
                        uoWMessageList.add(chat)
                        scrollDown!!.scrollTo(0, scrollDown!!.bottom)
                    }
                }
                Log.d("taguowmessageList", uoWMessageList.toString())
                val uoWMessageAdapter = UoWMessageAdapter(this@UoWMessagingActivity, uoWMessageList)

                messageRecyclerView?.adapter = uoWMessageAdapter
                Log.d("tag22", messageRecyclerView.toString())
                messageRecyclerView?.scrollToPosition(uoWMessageList.size-1)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val resetUnreadMessage = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId).child(friendUserId)

        val unreadCounter = resetUnreadMessage.orderByChild("receiverMessageStatus").equalTo("unread")
        unreadCounter.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{it->
                    val unreadKey = it.key.toString()
                    Log.d("tag232", unreadKey)
                    resetUnreadMessage.child(unreadKey).child("receiverMessageStatus").setValue("read")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }



    private fun sendMessage(message: String) {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val sendMessageDatabase = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId).child(friendUserId!!)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.now().format(formatter)
        Log.d("tagdatetime", dateTime.toString())

        val messagingContent: HashMap <String, String> = HashMap()
        messagingContent["senderUoWMessageId"] = userId
        messagingContent["receiverUoWMessageId"] = friendUserId!!
        messagingContent["message"] = message
        messagingContent["messageDateTime"] = dateTime
        messagingContent["senderMessageStatus"] = "read"
        messagingContent["receiverMessageStatus"] = "unread"


        sendMessageDatabase.push().setValue(messagingContent)

        val sendMessageDatabaseFriendUser = FirebaseDatabase.getInstance().getReference("UoWMessage").child(friendUserId!!).child(userId)

        val messagingContentFriendUser: HashMap <String, String> = HashMap()
        messagingContentFriendUser["senderUoWMessageId"] = userId
        messagingContentFriendUser["receiverUoWMessageId"] = friendUserId!!
        messagingContentFriendUser["message"] = message
        messagingContentFriendUser["messageDateTime"] = dateTime
        messagingContentFriendUser["senderMessageStatus"] = "read"
        messagingContentFriendUser["receiverMessageStatus"] = "unread"

        sendMessageDatabaseFriendUser.push().setValue(messagingContentFriendUser)

    }
}