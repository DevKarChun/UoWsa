package com.example.uowsa.discussionboard.dbactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.activity.UoWMessageActivity
import com.example.uowsa.activity.HomeActivity
import com.example.uowsa.activity.ProfileActivity
import com.example.uowsa.activity.SettingsActivity
import com.example.uowsa.adapter.dbadapter.DBMakeNewFriendsAdapter
import com.example.uowsa.usermodel.dbmodel.DBMakeNewFriends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBMakeNewFriendsActivity : AppCompatActivity() {

    var dBMakeNewFriendsList = ArrayList<DBMakeNewFriends>()
    private lateinit var auth: FirebaseAuth
    private var scrollDown: ScrollView? = null
    private var dBMakeNewFriendsRecyclerView: RecyclerView? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dbmake_new_friends)

        auth = Firebase.auth

        val deleteAllMessagesBtn = findViewById<ImageButton>(R.id.deleteAllMessagesBtn)
        scrollDown = findViewById(R.id.scrollDown)
        dBMakeNewFriendsRecyclerView = findViewById(R.id.dBMakeNewFriendsRecyclerView)
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
        dBMakeNewFriendsRecyclerView?.layoutManager = layoutManager
        dBMakeNewFriendsRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        readDBMakeNewFriends()

        dBMakeNewFriendsRecyclerView?.scrollToPosition(dBMakeNewFriendsList.size-1)

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

        val userRef = FirebaseDatabase.getInstance().getReference("DBMakeNewFriends")

        val userMessageDelete = userRef.orderByChild("senderDBMakeNewFriendsId").equalTo(userId)
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

    private fun sendMessage(message: String) {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid
        val sendMessageDatabase = FirebaseDatabase.getInstance().getReference("DBMakeNewFriends")

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val dateTime = LocalDateTime.now().format(formatter)
        Log.d("tagdatetime", dateTime.toString())

        val messagingContent: HashMap <String, String> = HashMap()
        messagingContent["senderDBMakeNewFriendsId"] = userId
        messagingContent["DBMakeNewFriendsMessage"] = message
        messagingContent["DBMakeNewFriendsMessageDateTime"] = dateTime

        sendMessageDatabase.push().setValue(messagingContent)
    }

    private fun readDBMakeNewFriends() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String = user!!.uid

        val readMessageDatabase = FirebaseDatabase.getInstance().getReference("DBMakeNewFriends")

        readMessageDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dBMakeNewFriendsList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(DBMakeNewFriends::class.java)
                    Log.d("tagChat", chat.toString())
                    Log.d("tagUserId", userId)
                    Log.d("tagsenderId", chat!!.senderDBMakeNewFriendsId)
                    Log.d("tagfriendId", chat.DBMakeNewFriendsMessage)
                    Log.d("tagmessagetime", chat.DBMakeNewFriendsMessageDateTime)

                    dBMakeNewFriendsList.add(chat)
                    scrollDown!!.scrollTo(0, scrollDown!!.bottom)
                }
                val dBMakeNewFriendsAdapter = DBMakeNewFriendsAdapter(this@DBMakeNewFriendsActivity, dBMakeNewFriendsList)

                dBMakeNewFriendsRecyclerView?.adapter = dBMakeNewFriendsAdapter
                dBMakeNewFriendsRecyclerView?.scrollToPosition(dBMakeNewFriendsList.size-1)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}