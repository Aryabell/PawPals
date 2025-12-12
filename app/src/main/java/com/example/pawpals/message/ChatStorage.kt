package com.example.pawpals.message

import com.example.pawpals.message.ChatMessage

object ChatStorage {
    private val chatMap = mutableMapOf<String, MutableList<ChatMessage>>()

    fun getMessages(userName: String): MutableList<ChatMessage> {
        return chatMap.getOrPut(userName) { mutableListOf() }
    }

    fun addMessage(userName: String, message: ChatMessage) {
        val list = chatMap.getOrPut(userName) { mutableListOf() }
        list.add(message)
    }
}
