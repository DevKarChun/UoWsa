package com.example.uowsa.adapter.generaladapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.R
import com.example.uowsa.usermodel.generalmodel.AdminMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminMessageAdapter (private val context: Context, private val adminMessageList: ArrayList<AdminMessage>) : RecyclerView.Adapter<AdminMessageAdapter.ViewHolder>(){
    private val messageLeft = 0
    private val messageRight = 1
    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == messageRight) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = adminMessageList[position]
        holder.txtMessage.text = chat.message

        if (chat.senderAdminMessageId == firebaseUser!!.uid){
            holder.layoutUserMessage.setOnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
                val dialog: AlertDialog = builder.setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Cancel") {
                            dialog, _ ->
//                        Toast.makeText(context, "you have pressed cancel", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Delete") { dialog, _ ->
//                        Toast.makeText(context, "you have pressed delete", Toast.LENGTH_SHORT).show()
                        deleteMessage(position)
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

            }
        }
    }

    private fun deleteMessage(position: Int) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val msgDateTime = adminMessageList[position].messageDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("contactAdmin").child(userId).child(adminMessageList[position].receiverAdminMessageId)
        val databaseDateTimeMsgFriend = FirebaseDatabase.getInstance().getReference("contactAdmin").child(adminMessageList[position].receiverAdminMessageId).child(userId)

        val timeUser = databaseDateTimeMsgUser.orderByChild("messageDateTime").equalTo(msgDateTime)
        timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot12: DataSnapshot in snapshot.children){
                    if (dataSnapShot12.child("senderAdminMessageId").getValue() == userId){
                        dataSnapShot12.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val timeFriend = databaseDateTimeMsgFriend.orderByChild("messageDateTime").equalTo(msgDateTime)
        timeFriend.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot12: DataSnapshot in snapshot.children){
                    if (dataSnapShot12.child("senderAdminMessageId").getValue() == userId){
                        dataSnapShot12.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun getItemCount(): Int {
        return adminMessageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.message)
        val layoutUserMessage: LinearLayout = view.findViewById(R.id.layoutUserMessage)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (adminMessageList[position].senderAdminMessageId == firebaseUser!!.uid) {
            messageRight
        } else {
            messageLeft
        }
    }
}