package com.example.uowsa.usermodel.generalmodel

data class UoWMessage (var senderUoWMessageId: String = "",
                       var receiverUoWMessageId: String = "",
                       var message: String = "",
                       var messageDateTime: String = "",
                       var senderMessageStatus: String = "",
                       var receiverMessageStatus: String = "")