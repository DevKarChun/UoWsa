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
import com.example.uowsa.discussionboard.forummessaging.CavendishCampusForumMessagingActivity
import com.example.uowsa.R
import com.example.uowsa.usermodel.forummodel.CavendishCampusForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CavendishCampusForumAdapter (private val context: Context, private val cavendishCampusForumList: ArrayList <CavendishCampusForum>) : RecyclerView.Adapter<CavendishCampusForumAdapter.ViewHolder>(){
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

    override fun getItemCount(): Int {
        return cavendishCampusForumList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studyList = cavendishCampusForumList[position]
        holder.titleTxt.text = studyList.cavendishCampusTitle
        holder.datePlaceholder.text = "Date: "
        holder.date.text = studyList.cavendishCampusDate

        holder.userPlaceholder.text = "Posted by: "
        val id = studyList.cavendishCampusCreatorId
        val database = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userName")
        database.get().addOnSuccessListener {
            val username = it.value.toString()
            holder.user.text = username
        }

        holder.forumLayout.setOnClickListener {
            val intent = Intent(context, CavendishCampusForumMessagingActivity::class.java)
            intent.putExtra("creatorId", studyList.cavendishCampusCreatorId)
            intent.putExtra("studyTitle", studyList.cavendishCampusTitle)
            intent.putExtra("studyDescription", studyList.cavendishCampusDescription)
            intent.putExtra("studyDate", studyList.cavendishCampusDate)
            intent.putExtra("createdDateTime", studyList.cavendishCampusCreatedDateTime)
            context.startActivity(intent)
        }

        holder.bin.setOnClickListener {
            holder.bin.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
                val dialog: AlertDialog = builder.setTitle("Delete Forum")
                    .setMessage("Are you sure you want to delete this Forum? All data inside this forum will be erased.?")
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
        val msgDateTime = cavendishCampusForumList[position].cavendishCampusCreatedDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("CavendishCampusForum")

        if (cavendishCampusForumList[position].cavendishCampusCreatorId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("cavendishCampusCreatedDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("cavendishCampusCreatorId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        val userRef = FirebaseDatabase.getInstance().getReference("CavendishCampusForum").child(cavendishCampusForumList[position].cavendishCampusCreatorId).child(cavendishCampusForumList[position].cavendishCampusCreatedDateTime)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot01: DataSnapshot in snapshot.children){
                    dataSnapshot01.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //data erased

        if (cavendishCampusForumList[position].cavendishCampusCreatorId == userId){
            val databaseUserForumRemoveContents = FirebaseDatabase.getInstance().getReference("AccommodationMessage").child(cavendishCampusForumList[position].cavendishCampusCreatorId).child(cavendishCampusForumList[position].cavendishCampusCreatedDateTime)

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

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (cavendishCampusForumList[position].cavendishCampusCreatorId == firebaseUser!!.uid) {
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