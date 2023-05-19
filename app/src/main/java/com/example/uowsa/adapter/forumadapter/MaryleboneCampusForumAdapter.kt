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
import com.example.uowsa.discussionboard.forummessaging.MaryleboneCampusForumMessagingActivity
import com.example.uowsa.R
import com.example.uowsa.usermodel.forummodel.MaryleboneCampusForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MaryleboneCampusForumAdapter (private val context: Context, private val maryleboneCampusForumList: ArrayList <MaryleboneCampusForum>) : RecyclerView.Adapter<MaryleboneCampusForumAdapter.ViewHolder>(){
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
        return maryleboneCampusForumList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studyList = maryleboneCampusForumList[position]
        holder.titleTxt.text = studyList.maryleboneCampusTitle
        holder.datePlaceholder.text = "Date: "
        holder.date.text = studyList.maryleboneCampusDate

        holder.userPlaceholder.text = "Posted by: "
        val id = studyList.maryleboneCampusCreatorId
        val database = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userName")
        database.get().addOnSuccessListener {
            val username = it.value.toString()
            holder.user.text = username
        }

        holder.forumLayout.setOnClickListener {
            val intent = Intent(context, MaryleboneCampusForumMessagingActivity::class.java)
            intent.putExtra("creatorId", studyList.maryleboneCampusCreatorId)
            intent.putExtra("studyTitle", studyList.maryleboneCampusTitle)
            intent.putExtra("studyDescription", studyList.maryleboneCampusDescription)
            intent.putExtra("studyDate", studyList.maryleboneCampusDate)
            intent.putExtra("createdDateTime", studyList.maryleboneCampusCreatedDateTime)
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
        val msgDateTime = maryleboneCampusForumList[position].maryleboneCampusCreatedDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("MaryleboneCampusForum")

        if (maryleboneCampusForumList[position].maryleboneCampusCreatorId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("maryleboneCampusCreatedDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("maryleboneCampusCreatorId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        val userRef = FirebaseDatabase.getInstance().getReference("MaryleboneCampusForum").child(maryleboneCampusForumList[position].maryleboneCampusCreatorId).child(maryleboneCampusForumList[position].maryleboneCampusCreatedDateTime)

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

        if (maryleboneCampusForumList[position].maryleboneCampusCreatorId == userId){
            val databaseUserForumRemoveContents = FirebaseDatabase.getInstance().getReference("MaryleboneCampusMessage").child(maryleboneCampusForumList[position].maryleboneCampusCreatorId).child(maryleboneCampusForumList[position].maryleboneCampusCreatedDateTime)

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
        return if (maryleboneCampusForumList[position].maryleboneCampusCreatorId == firebaseUser!!.uid) {
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