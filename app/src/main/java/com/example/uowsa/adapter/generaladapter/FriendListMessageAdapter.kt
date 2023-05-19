package com.example.uowsa.adapter.generaladapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uowsa.R
import com.example.uowsa.activity.UoWMessagingActivity
import com.example.uowsa.usermodel.generalmodel.FriendsList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class FriendListMessageAdapter (private val context: Context,
                                private val FriendRequestMessageList: ArrayList <FriendsList>) :
    RecyclerView.Adapter<FriendListMessageAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friendmessage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val userId: String = user!!.uid
        val friendMessageList = FriendRequestMessageList[position]
        holder.txtUserName.text = ""
        holder.txtStatus.text = ""
        Glide.with(holder.itemView.context).load("").into(holder.userImage)

        //refresh username/status/image if user changes these values after friends are set.
        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(friendMessageList.friendUserId)
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
        databaseRefresh.child("connectivityStatus").get().addOnSuccessListener {
            val connectivityStatusInfo = it.value.toString()
            if (connectivityStatusInfo == "Online"){
                holder.connectivityStatus.setBackgroundResource(R.drawable.connectivity_status_online)
            }else{
                holder.connectivityStatus.setBackgroundResource(R.drawable.connectivity_status_offline)
            }
        }

        val sendMessageDatabase = FirebaseDatabase.getInstance().getReference("UoWMessage").child(userId).child(friendMessageList.friendUserId)
        var unreadCount :Long
        val unreadCounter = sendMessageDatabase.orderByChild("receiverMessageStatus").equalTo("unread")
        unreadCounter.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                unreadCount = snapshot.childrenCount
                if (unreadCount > 0){
                    val unreadCountToString = unreadCount.toString()
                    holder.unreadMessage.text = unreadCountToString
                    holder.unreadMessage.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        holder.layoutFriendMessage.setOnClickListener {
            val intent = Intent(context, UoWMessagingActivity::class.java)
            intent.putExtra("friendUserId",friendMessageList.friendUserId)
            Log.d("taguserid", friendMessageList.friendUserId)
            intent.putExtra("friends", friendMessageList.friends)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return FriendRequestMessageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtStatus: TextView = view.findViewById(R.id.status)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val unreadMessage : TextView = view.findViewById(R.id.unreadMessageCounter)
        val connectivityStatus: TextView = view.findViewById(R.id.connectivityStatus)
        val layoutFriendMessage: LinearLayout = view.findViewById(R.id.layoutFriendMessage)
    }
}