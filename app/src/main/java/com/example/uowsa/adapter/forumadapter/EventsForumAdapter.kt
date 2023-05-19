package com.example.uowsa.adapter.forumadapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uowsa.discussionboard.forummessaging.EventsForumMessagingActivity
import com.example.uowsa.R
import com.example.uowsa.usermodel.forummodel.EventsForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventsForumAdapter (private val context: Context, private val EventsForumList: ArrayList <EventsForum>) : RecyclerView.Adapter<EventsForumAdapter.ViewHolder>(){
    var firebaseUser: FirebaseUser? = null
    private val postCreator = 0
    private val post = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == postCreator){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forumpostcreator, parent, false)
            ViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forumpost, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventsList = EventsForumList[position]
        holder.titleTxt.text = eventsList.eventsTitle
        holder.datePlaceholder.text = "Date: "
        holder.date.text = eventsList.eventsDate

        holder.userPlaceholder.text = "Posted by: "
        val id = eventsList.eventsCreatorId
        val database = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userName")
        database.get().addOnSuccessListener {
            val username = it.value.toString()
            holder.user.text = username
        }

        holder.forumLayout.setOnClickListener {
            val intent = Intent(context, EventsForumMessagingActivity::class.java)
            intent.putExtra("creatorId", eventsList.eventsCreatorId)
            intent.putExtra("title", eventsList.eventsTitle)
            intent.putExtra("description", eventsList.eventsDescription)
            intent.putExtra("date", eventsList.eventsDate)
            intent.putExtra("createdDateTime", eventsList.eventsCreatedDateTime)
            context.startActivity(intent)
        }

        holder.bin.setOnClickListener {
            holder.bin.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
                val dialog: AlertDialog = builder.setTitle("Delete Forum")
                    .setMessage("Are you sure you want to delete this Forum? All data inside this forum will be erased.")
                    .setPositiveButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Delete") { dialog, _ ->
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
        val msgDateTime = EventsForumList[position].eventsCreatedDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("EventsForum")

        if (EventsForumList[position].eventsCreatorId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("eventsCreatedDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("eventsCreatorId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        val userRef = FirebaseDatabase.getInstance().getReference("EventsForum").child(EventsForumList[position].eventsCreatorId).child(EventsForumList[position].eventsCreatedDateTime)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot01: DataSnapshot in snapshot.children){
                    dataSnapshot01.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //data erased

        if (EventsForumList[position].eventsCreatorId == userId){
            val databaseUserForumRemoveContents = FirebaseDatabase.getInstance().getReference("EventsMessage").child(EventsForumList[position].eventsCreatorId).child(EventsForumList[position].eventsCreatedDateTime)

            databaseUserForumRemoveContents.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot01: DataSnapshot in snapshot.children){
                        dataSnapshot01.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    override fun getItemCount(): Int {
        return EventsForumList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (EventsForumList[position].eventsCreatorId == firebaseUser!!.uid) {
            postCreator
        } else {
            post
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleTxt: TextView = view.findViewById(R.id.title)
        val datePlaceholder: TextView = view.findViewById(R.id.datePlaceholder)
        val date: TextView = view.findViewById(R.id.date)
        val userPlaceholder: TextView = view.findViewById(R.id.userPlaceholder)
        val user: TextView = view.findViewById(R.id.user)
        val bin: ImageButton = view.findViewById(R.id.bin)
        val forumLayout: LinearLayout = view.findViewById(R.id.forumLayout)
    }
}