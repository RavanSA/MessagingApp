package com.project.messagingapp.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ContactItemBinding
import androidx.databinding.library.baseAdapters.BR

class CustomContactAdapter :
    RecyclerView.Adapter<CustomContactAdapter.CustomContactView>() {

    var appContacts: List<UserModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CustomContactAdapter.CustomContactView {
        val layoutinflater = LayoutInflater.from(parent.context)

        val contactItemBinding: ContactItemBinding = DataBindingUtil.inflate(layoutinflater,
            R.layout.contact_item, parent,false)

        return CustomContactView(contactItemBinding)
    }

    override fun onBindViewHolder(holder: CustomContactAdapter.CustomContactView, position: Int) {
//        val userModel = appContacts[position]
//        holder.item.contactItem = userModel
        holder.item.contactItem = appContacts[position]
    }

    override fun getItemCount(): Int {
        return appContacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<UserModel>?) {
        appContacts = items ?: emptyList()
        notifyDataSetChanged()
    }

    inner class CustomContactView(val item: ContactItemBinding) : RecyclerView.ViewHolder(item.root)
}

object ContactRecyclerAdapter  {
            @JvmStatic
            @BindingAdapter("contactItem")
            fun bindContactItem(recyclerView: RecyclerView, contactItem: List<UserModel>?) {
                val adapter = getOrCreateAdapter(recyclerView)
                adapter.updateItems(contactItem)
            }

            private fun getOrCreateAdapter(recyclerView: RecyclerView): CustomContactAdapter {
                return if (recyclerView.adapter != null && recyclerView.adapter is CustomContactAdapter) {
                    recyclerView.adapter as CustomContactAdapter
                } else {
                    val bindableRecyclerAdapter = CustomContactAdapter()
                    recyclerView.adapter = bindableRecyclerAdapter
                    bindableRecyclerAdapter
                }
            }
    }
