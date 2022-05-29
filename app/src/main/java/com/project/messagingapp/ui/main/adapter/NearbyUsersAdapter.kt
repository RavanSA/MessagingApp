package com.project.messagingapp.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import kotlinx.android.synthetic.main.activity_nearby_user_fragment.view.*

import kotlinx.android.synthetic.main.nearby_user_item.view.*

class NearbyUsersAdapter(
    private val nearbyUserList: MutableList<UserModel>
): RecyclerView.Adapter<NearbyUsersAdapter.ViewHolder>()  {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("NEARBYVIEWHODER", nearbyUserList.toString())

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.nearby_user_item, parent, false)
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: MutableList<UserModel>){
        Log.d("SERDATA", nearbyUserList.toString())
        nearbyUserList.run {
            clear()
            addAll(data)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.UserName.text = nearbyUserList[position].name
        holder.itemView.UserStatus.text = nearbyUserList[position].status

//        if(!nearbyUserList[position].image.isNullOrEmpty()) {
//            Glide.with(holder.itemView.context).load(nearbyUserList[position].image)
//                .into(holder.itemView.nearbyImage)
//        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, MessageActivity::class.java)
            intent.putExtra("id_receiver",nearbyUserList[position].uid)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return if(nearbyUserList.size == 0){
            0
        }  else {
            nearbyUserList.size
        }
    }
}