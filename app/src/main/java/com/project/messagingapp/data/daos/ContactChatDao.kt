package com.project.messagingapp.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ContactChatList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface ContactChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContactChatList(contactChatList:ContactChatList)

    @Query("SELECT * FROM contact_chat_list WHERE is_blocked ='0'")
    fun getContactChatList(): Flow<MutableList<ContactChatList>>

    fun getContactChatListUntilChanged() =
        getContactChatList().distinctUntilChanged()

    @Query("UPDATE contact_chat_list SET message_date = :date, last_message = :message WHERE chatID = :chatID")
     fun contactLastMessageUpdate(date: String, message: String, chatID: String)

     @Query("UPDATE contact_chat_list SET is_blocked = :block WHERE receiver_id = :receiverID")
     fun blockUser(receiverID: String,block: String) : Unit

    @Query("SELECT * FROM CONTACT_CHAT_LIST WHERE is_blocked = '1'")
    fun getAllBlockedUser(): MutableList<ContactChatList>

}