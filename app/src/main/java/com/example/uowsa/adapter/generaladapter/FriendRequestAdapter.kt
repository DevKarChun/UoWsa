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
import com.example.uowsa.activity.RequestedUserActivity
import com.example.uowsa.usermodel.generalmodel.FriendRequest
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class FriendRequestAdapter (private val context: Context, private val FriendRequestList: ArrayList <FriendRequest>) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friendrequest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestFriendRequestList = FriendRequestList[position]
        holder.txtUserName.text = ""
        holder.txtStatus.text = ""
        Glide.with(holder.itemView.context).load("").into(holder.userImage)

        //refresh username/status/image if user changes these values after friends are set.
        val databaseRefresh = FirebaseDatabase.getInstance().getReference("Users").child(requestFriendRequestList.senderUserId)
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

        holder.layoutFriendRequest.setOnClickListener {
            val intent = Intent(context, RequestedUserActivity::class.java)
            intent.putExtra("senderUserId",requestFriendRequestList.senderUserId)
            intent.putExtra("receiverFriendUserId", requestFriendRequestList.receiverFriendUserId)
            intent.putExtra("friendAccepted", requestFriendRequestList.requestAccepted)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return FriendRequestList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtStatus: TextView = view.findViewById(R.id.status)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val layoutFriendRequest: LinearLayout = view.findViewById(R.id.layoutFriendRequest)
    }
}