package com.project.messagingapp.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.data.model.ContactListRoom

@Dao
interface ContactListDao {

    @Insert
    fun addReceiverInformation(contactListRoom: ContactListRoom)

    @Query("SELECT * FROM contact_list INNER JOIN chat_list ON chat_list.receiverID = contact_list.receiver_id")
    fun getAllContactList() : MutableList<ContactChatList>

}