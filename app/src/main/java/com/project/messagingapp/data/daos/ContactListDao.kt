package com.project.messagingapp.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.messagingapp.data.model.ContactListRoom

@Dao
interface ContactListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReceiverInformation(contactListRoom: ContactListRoom)

    @Query("SELECT * FROM contact_list")
    fun getContactList(): List<ContactListRoom>

    @Query("SELECT * FROM contact_list WHERE receiver_id = :receiverID")
    fun getContactListByReceiverID(receiverID: String): ContactListRoom

}