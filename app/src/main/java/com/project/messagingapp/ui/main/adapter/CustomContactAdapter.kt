package com.project.messagingapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ContactItemBinding
import com.project.messagingapp.databinding.SettingListBinding

class CustomContactAdapter(private var appContacts: ArrayList<UserModel>):
    RecyclerView.Adapter<CustomContactAdapter.CustomContactView>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomContactView {
        val layoutInflater = LayoutInflater.from(parent.context)

        val contactItemBinding: ContactItemBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.contact_item, parent,false)

        return CustomContactView(contactItemBinding)
    }

    override fun onBindViewHolder(holder: CustomContactView, position: Int) {
        val userModel = appContacts[position]
        holder.item.userModel = userModel
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    class CustomContactView(val item: ContactItemBinding): RecyclerView.ViewHolder(item.root) {

    }

}