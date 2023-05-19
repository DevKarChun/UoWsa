package com.example.uowsa.adapter.generaladapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uowsa.activity.AdminMessagingActivity
import com.example.uowsa.R

import com.example.uowsa.usermodel.generalmodel.User
import de.hdodenhof.circleimageview.CircleImageView

class AdminAdapter (private val context: Context, private val adminList: ArrayList <User>) : RecyclerView.Adapter<AdminAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = adminList[position]
        holder.txtUserName.text = user.userName
        holder.txtStatus.text = user.status
        Glide.with(holder.itemView.context).load(user.profileImage).into(holder.userImage)

        holder.layoutUser.setOnClickListener {
            val intent = Intent(context, AdminMessagingActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("userName",user.userName)
            intent.putExtra("profileImage", user.profileImage)
            intent.putExtra("status", user.status)
            intent.putExtra("studyCourse", user.studyCourse)
            intent.putExtra("campusLocation", user.campusLocation)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return adminList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtStatus: TextView = view.findViewById(R.id.status)
        val userImage: CircleImageView = view.findViewById(R.id.userImage)
        val layoutUser: LinearLayout = view.findViewById(R.id.layoutUser)
    }
}