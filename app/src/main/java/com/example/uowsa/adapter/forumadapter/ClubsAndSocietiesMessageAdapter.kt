package com.example.uowsa.adapter.forumadapter

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
import com.example.uowsa.discussionboard.forumprofile.ClubsAndSocietiesProfileActivity
import com.example.uowsa.usermodel.forummessagingmodel.ClubsAndSocietiesMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ClubsAndSocietiesMessageAdapter (private val context: Context, private val clubsAndSocietiesMessageList: ArrayList<ClubsAndSocietiesMessage>) : RecyclerView.Adapter<ClubsAndSocietiesMessageAdapter.ViewHolder>(){
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clubsAndSocietiesMessage = clubsAndSocietiesMessageList[position]
        holder.txtMessage.text = clubsAndSocietiesMessage.clubsAndSocietiesMessage
        val database = FirebaseDatabase.getInstance().getReference("Users").child(clubsAndSocietiesMessage.senderClubsAndSocietiesId)
        database.child("profileImage").get().addOnSuccessListener {
            val image = it.value.toString()
            Glide.with(holder.itemView.context).load(image).into(holder.userImage)
        }

        if (clubsAndSocietiesMessage.senderClubsAndSocietiesId == firebaseUser!!.uid){
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

        if (clubsAndSocietiesMessage.senderClubsAndSocietiesId != firebaseUser!!.uid){
            holder.circleImageLayout.setOnClickListener {
                val intent = Intent(context, ClubsAndSocietiesProfileActivity::class.java)
                intent.putExtra("friendUserId",clubsAndSocietiesMessage.senderClubsAndSocietiesId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    private fun deleteMessage(position: Int) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val msgDateTime = clubsAndSocietiesMessageList[position].clubsAndSocietiesMessageDateTime

        val databaseDateTimeMsgUser = FirebaseDatabase.getInstance().getReference("ClubsAndSocietiesMessage").child(clubsAndSocietiesMessageList[position].clubsAndSocietiesCreatorId).child(clubsAndSocietiesMessageList[position].clubsAndSocietiesCreatedDateTime)

        if (clubsAndSocietiesMessageList[position].senderClubsAndSocietiesId == userId){
            val timeUser = databaseDateTimeMsgUser.orderByChild("clubsAndSocietiesMessageDateTime").equalTo(msgDateTime)
            timeUser.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapShot12: DataSnapshot in snapshot.children){
                        if (dataSnapShot12.child("senderClubsAndSocietiesId").getValue() == userId){
                            dataSnapShot12.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.message)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val dbUserMessageLayout: LinearLayout = view.findViewById(R.id.dbUserMessageLayout)
        val circleImageLayout: LinearLayout = view.findViewById(R.id.circleImageLayout)

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (clubsAndSocietiesMessageList[position].senderClubsAndSocietiesId == firebaseUser!!.uid) {
            messageRight
        } else {
            messageLeft
        }
    }

    override fun getItemCount(): Int {
        return clubsAndSocietiesMessageList.size
    }
}