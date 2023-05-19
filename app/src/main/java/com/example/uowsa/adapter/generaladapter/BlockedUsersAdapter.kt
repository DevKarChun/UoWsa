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
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.usermodel.generalmodel.BlockedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class BlockedUsersAdapter (private val context: Context, private val BlockedList: ArrayList <BlockedList>): RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_blockedusers, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blockedUserList = BlockedList[position]

        holder.txtUserName.text = ""
        holder.txtStatus.text = ""
        Glide.with(holder.itemView.context).load("").into(holder.userImage)

        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(blockedUserList.userBlockedUserId)
        databaseRefresh.child("userName").get().addOnSuccessListener {
            val usernameRefresh = it.value.toString()
            holder.txtUserName.text = usernameRefresh
        }
        databaseRefresh.child("status").get().addOnSuccessListener {
            val statusRefresh = it.value.toString()
            holder.txtStatus.text = statusRefresh
        }
        databaseRefresh.child("profileImage").get().addOnSuccessListener {
            val imageRefresh = it.value.toString()
            Glide.with(holder.itemView.context).load(imageRefresh).into(holder.userImage)
        }

        holder.layoutBlockedUser.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.DialogTheme)
            val unblockUser = FirebaseDatabase.getInstance().getReference("Users").child(blockedUserList.userBlockedUserId)
            unblockUser.child("userName").get().addOnSuccessListener {
                val username = it.value.toString()
                val dialog: AlertDialog = builder.setTitle("Unblock User")
                    .setMessage("Are you sure you want to unblock $username?")
                    .setPositiveButton("Cancel") {
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Unblock") { dialog, _ ->
                        unblockFriend(position)
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }


    }

    private fun unblockFriend(position:Int) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val databaseFirebase = FirebaseDatabase.getInstance().getReference("BlockedList")
        val unblockUser = databaseFirebase.child(userId).child(BlockedList[position].userBlockedUserId)

        //remove from blocked list
        unblockUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    dataSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //add friend to user list
        val addFriendToUserFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(userId).child(BlockedList[position].userBlockedUserId)
        addFriendToUserFriendList.child("friendUserId").setValue(BlockedList[position].userBlockedUserId)
        addFriendToUserFriendList.child("friends").setValue("Friend")

        //add user to friend list
        val addFriendToOtherFriendList = FirebaseDatabase.getInstance().getReference("FriendsList").child(BlockedList[position].userBlockedUserId).child(userId)
        addFriendToOtherFriendList.child("friendUserId").setValue(userId)
        addFriendToOtherFriendList.child("friends").setValue("Friend")

    }

    override fun getItemCount(): Int {
        return BlockedList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtStatus: TextView = view.findViewById(R.id.status)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val layoutBlockedUser: LinearLayout = view.findViewById(R.id.layoutBlockedUser)
    }

}