package com.project.messagingapp.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.messagingapp.R
import com.project.messagingapp.data.model.UserModel
import com.project.messagingapp.databinding.ContactItemBinding
import androidx.databinding.library.baseAdapters.BR
import com.project.messagingapp.ui.main.view.activities.ContactUserInfo
import com.project.messagingapp.ui.main.view.activities.MessageActivity
import com.project.messagingapp.ui.main.view.fragments.VerifyNum
import java.util.*
import kotlin.collections.ArrayList

class CustomContactAdapter(
    appUserContacts: List<UserModel>
) :
    RecyclerView.Adapter<CustomContactAdapter.CustomContactView>(),Filterable {

    var appContacts: List<UserModel> = appUserContacts
    private var allContact: List<UserModel> = appContacts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CustomContactAdapter.CustomContactView {
        val layoutInflater = LayoutInflater.from(parent.context)

        val contactItemBinding: ContactItemBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.contact_item, parent,false)

        return CustomContactView(contactItemBinding)
    }

    override fun onBindViewHolder(holder: CustomContactAdapter.CustomContactView, position: Int) {
        val userModel = appContacts[position]

        holder.item.contactItem = appContacts[position]

        holder.item.imgContactInfo.setOnClickListener {

            val intent = Intent(it.context, ContactUserInfo::class.java)
            intent.putExtra("UID", userModel.uid)
            it.context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,MessageActivity::class.java)
            intent.putExtra("id_receiver",userModel.uid)
//            intent.putExtra("image_receiver",userModel.image)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return appContacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<UserModel>?) {
        (items ?: emptyList()).also { appContacts = it }
        notifyDataSetChanged()
    }

    inner class CustomContactView(val item: ContactItemBinding) : RecyclerView.ViewHolder(item.root)

    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val contactSearch = constraint.toString()
                if(contactSearch.isEmpty()){
                    allContact = appContacts
                    Log.d("ALLCONTACTFILTER",allContact.toString())
                } else{
                    val filterContact = ArrayList<UserModel>()
                    for(friends in appContacts){
                        if (friends.name!!.toLowerCase(Locale.ROOT).trim()
                                .contains(contactSearch.toLowerCase(Locale.ROOT).trim()))
                                    filterContact.add(friends)
                    }
                    allContact = filterContact
                    Log.d("ALLCONTACTELSE",allContact.toString())
                }
                val filterResult = FilterResults()
                filterResult.values = allContact
                Log.d("FilterResult",filterResult.toString())
                return filterResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                allContact = results!!.values as List<UserModel>
                Log.d("PUBLISHRESULT",allContact.toString())
                updateItems(allContact)
            }

        }
    }
}
