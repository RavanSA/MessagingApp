package com.project.messagingapp.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ContactChatList
import com.project.messagingapp.data.model.ContactListRoom
import com.project.messagingapp.data.model.ContactListandChatList
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReceiverInformation(contactListRoom: ContactListRoom)

    @Query("SELECT * FROM contact_list")
    fun getContactList(): List<ContactListRoom>

    @Query("SELECT * FROM contact_list INNER JOIN chat_list ON chat_list.receiver_id = contact_list.receiver_id")
     fun getAllContactList() : Flow<MutableList<ContactChatList>>

    @Query("SELECT * FROM contact_list JOIN chat_list ON chat_list.receiver_id = contact_list.receiver_id")
    fun getContactListAndChatList(): MutableList<ContactListandChatList>

}