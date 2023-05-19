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
import com.example.uowsa.activity.FriendBlockOrRemoveActivity
import com.example.uowsa.R

import com.example.uowsa.usermodel.generalmodel.FriendsList

import com.google.firebase.database.FirebaseDatabase

import de.hdodenhof.circleimageview.CircleImageView

class FriendsListAdapter (private val context: Context, private val FriendsList: ArrayList <FriendsList>): RecyclerView.Adapter<FriendsListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friendslist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestFriendsList = FriendsList[position]

//        holder.txtUserName.text = requestFriendsList.friendUserName
//        Log.d("tagUser", requestFriendsList.friendUserName)
//        holder.txtStatus.text = requestFriendsList.friendStatus
//        Log.d("tagStatus", requestFriendsList.friendStatus)
//        Glide.with(holder.itemView.context).load(requestFriendsList.friendProfileImage).into(holder.userImage)

        holder.txtUserName.text = ""
        holder.txtStatus.text = ""
        Glide.with(holder.itemView.context).load("").into(holder.userImage)

        //refresh username/status/image if user changes these values after friends are set.
        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(requestFriendsList.friendUserId)
        databaseRefresh.child("userName").get().addOnSuccessListener {
            val usernameRefresh = it.value.toString()
            Log.d("tagusernamerefresh", usernameRefresh)
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

        holder.layoutFriendsList.setOnClickListener{
            val intent = Intent(context, FriendBlockOrRemoveActivity::class.java)
            intent.putExtra("friendUserId",requestFriendsList.friendUserId)
            intent.putExtra("friends",requestFriendsList.friends)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return FriendsList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtStatus: TextView = view.findViewById(R.id.status)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val connectivityStatus: TextView = view.findViewById(R.id.connectivityStatus)
        val layoutFriendsList: LinearLayout = view.findViewById(R.id.layoutFriendsList)
    }
}