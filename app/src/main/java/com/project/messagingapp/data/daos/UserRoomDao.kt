package com.project.messagingapp.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.messagingapp.data.model.UserRoomModel


@Dao
interface UserRoomDao {

    @Insert
    suspend fun insertUser(user: UserRoomModel)

    @Query("UPDATE user_table SET name=:name")
    suspend fun updateUserLocalName(name: String)

    @Query("UPDATE user_table SET status=:updatedStatus")
    suspend fun updateUserLocalStatus(updatedStatus: String)

    @Query("UPDATE user_table SET profileImage=:newUpdatedProfileImage")
    suspend fun updateuserLocalProfileImage(newUpdatedProfileImage:String)

    @Delete
    suspend fun deleteUser(user:UserRoomModel)

    @Query("SELECT * from user_table")
    fun getUserRoom(): LiveData<UserRoomModel>


}