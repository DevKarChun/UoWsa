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
import com.example.uowsa.R
import com.example.uowsa.discussionboard.forummessaging.StudentUnionForumMessagingActivity
import com.example.uowsa.usermodel.forummodel.StudentUnionForum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentUnionForumAdapter (private val context: Context, private val StudentUnionForumList: ArrayList <StudentUnionForum>) : RecyclerView.Adapter<StudentUnionForumAdapter.ViewHolder>(){
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
        return StudentUnionForumList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studyList = StudentUnionForumList[position]
        holder.titleTxt.text = studyList.studentUnionTitle
        holder.datePlaceholder.text = "Date: "
        holder.date.text = studyList.studentUnionDate

        holder.userPlaceholder.text = "Posted by: "
        val id = studyList.studentUnionCreatorId
        val database = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userName")
        database.get().addOnSuccessListener {
            val username = it.value.toString()
            holder.user.text = username
        }

        holder.forumLayout.setOnClickListener {
            val intent = Intent(context, StudentUnionForumMessagingActivity::class.java)
            intent.putExtra("creatorId", studyList.studentUnionCreatorId)
            intent.putExtra("studyTitle", studyList.studentUnionTitle)
            intent.putExtra("studyDescription", studyList.studentUnionDescription)
            intent.putExtra("studyDate", studyList.studentUnionDate)
            intent.putExtra("createdDateTime", studyList.studentUnionCreatedDateTime)
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
        val msgDateTime = StudentUnionForumList[position].studentUnionCreatedDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("StudentUnionForum")

        if (StudentUnionForumList[position].studentUnionCreatorId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("studentUnionCreatedDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("studentUnionCreatorId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        val userRef = FirebaseDatabase.getInstance().getReference("StudentUnionForum").child(StudentUnionForumList[position].studentUnionCreatorId).child(StudentUnionForumList[position].studentUnionCreatedDateTime)

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

        if (StudentUnionForumList[position].studentUnionCreatorId == userId){
            val databaseUserForumRemoveContents = FirebaseDatabase.getInstance().getReference("StudentUnionMessage").child(StudentUnionForumList[position].studentUnionCreatorId).child(StudentUnionForumList[position].studentUnionCreatedDateTime)

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
        return if (StudentUnionForumList[position].studentUnionCreatorId == firebaseUser!!.uid) {
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