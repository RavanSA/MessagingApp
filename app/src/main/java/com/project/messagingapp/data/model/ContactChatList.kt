package com.project.messagingapp.data.model



//@Entity(
//    tableName = "contact_chat_list",
//
//    foreignKeys = [
//        ForeignKey(entity = ContactListRoom::class, parentColumns = ["receiver_id"], childColumns = ["receiverID"]),
//        ForeignKey(entity = ChatListRoom::class, parentColumns = ["receiverID"], childColumns = ["receiverID"])
//    ]
//)
data class ContactChatList(
    val contact_id: String = "",
    val receiver_id: String = "",
    val receiver_Name: String = "",
    val receiver_phone_number: String = "",
    val receiver_status: String = "",
    val receiver_image: String = "",
    val uid: String = "",
    val chatid: String = "",
    val message_date: String = "",
    val lastMessageOfChat: String = "",
    val receiverID: String = ""
) {

}