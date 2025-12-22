package com.example.pawpals.message

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.pawpals.api.ChatSessionManager
import com.example.pawpals.api.MessageApiClient
import com.example.pawpals.databinding.ActivityChatBinding
import com.google.android.material.appbar.MaterialToolbar

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.pawpals.R

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val currentUserId by lazy {
        ChatSessionManager(this).userId
    }

    private var chatId = 0
    private var receiverId = 0
    private var receiverName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarChat)
        setSupportActionBar(toolbar)

        receiverId = intent.getIntExtra("receiverId", 0)
        receiverName = intent.getStringExtra("receiverName") ?: "Chat"
        chatId = intent.getIntExtra("chatId", 0)

        if (receiverId == 0) {
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupSendButton()

        if (chatId == 0) {
            createChat()
        } else {
            loadMessages()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarChat)

        supportActionBar?.apply {
            title = receiverName
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarChat.setNavigationOnClickListener {
            finish()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages, currentUserId)
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvChat.adapter = adapter
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun createChat() {
        MessageApiClient.api.createOrGetChat(currentUserId, receiverId)
            .enqueue(object : Callback<Map<String, Any>> {

                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    chatId = (response.body()?.get("chat_id") as Double).toInt()
                    loadMessages()
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("CHAT", "Create chat failed", t)
                }
            })
    }

    private fun loadMessages() {
        MessageApiClient.api.getMessages(chatId)
            .enqueue(object : Callback<List<ChatMessage>> {

                override fun onResponse(
                    call: Call<List<ChatMessage>>,
                    response: Response<List<ChatMessage>>
                ) {
                    messages.clear()
                    messages.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()

                    if (messages.isNotEmpty()) {
                        binding.rvChat.scrollToPosition(messages.size - 1)
                    }
                }

                override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                    Log.e("CHAT", "Load messages failed", t)
                }
            })
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty() || chatId == 0) return

        MessageApiClient.api.sendMessage(chatId, currentUserId, text)
            .enqueue(object : Callback<Map<String, Any>> {

                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    val success = response.body()?.get("success") as? Boolean ?: false
                    if (success) {
                        binding.etMessage.text.clear()
                        loadMessages()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Log.e("CHAT", "Send error", t)
                }
            })
    }
}
