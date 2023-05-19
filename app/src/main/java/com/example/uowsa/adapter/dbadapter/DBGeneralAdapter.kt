package com.example.uowsa.adapter.dbadapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.discussionboard.dbprofile.DBGeneralProfileActivity
import com.example.uowsa.usermodel.dbmodel.DBGeneral
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class DBGeneralAdapter (private val context: Context, private val dbGeneralList: ArrayList<DBGeneral>) : RecyclerView.Adapter<DBGeneralAdapter.ViewHolder>(){
    private val messageLeft = 0
    private val messageRight = 1
    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == messageRight) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.db_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.db_item_left, parent, false)
            ViewHolder(view)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val dbGeneral = dbGeneralList[position]
        holder.txtMessage.text = dbGeneral.dbGeneralMessage
        val database = FirebaseDatabase.getInstance().getReference("Users").child(dbGeneral.senderDBGeneralId)
        database.child("profileImage").get().addOnSuccessListener {
            val image = it.value.toString()
            Glide.with(holder.itemView.context).load(image).into(holder.userImage)
        }

        if (dbGeneral.senderDBGeneralId == firebaseUser!!.uid){
            holder.dbUserMessageLayout.setOnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
                val dialog: AlertDialog = builder.setTitle("Delete Message")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Cancel") {
                            dialog, _ ->
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

        if (dbGeneral.senderDBGeneralId != firebaseUser!!.uid){
            holder.circleImageLayout.setOnClickListener {
                val intent = Intent(context, DBGeneralProfileActivity::class.java)
                intent.putExtra("friendUserId",dbGeneral.senderDBGeneralId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    private fun deleteMessage(position: Int) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val msgDateTime = dbGeneralList[position].dbGeneralMessageDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("DBGeneral")

        if (dbGeneralList[position].senderDBGeneralId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("dbGeneralMessageDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("senderDBGeneralId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    override fun getItemCount(): Int {
        return dbGeneralList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.message)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val dbUserMessageLayout: LinearLayout = view.findViewById(R.id.dbUserMessageLayout)
        val circleImageLayout: LinearLayout = view.findViewById(R.id.circleImageLayout)

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (dbGeneralList[position].senderDBGeneralId == firebaseUser!!.uid) {
            messageRight
        } else {
            messageLeft
        }
    }
}