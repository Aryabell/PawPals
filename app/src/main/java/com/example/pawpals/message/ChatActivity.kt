package com.example.pawpals.message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawpals.databinding.ActivityChatBinding
import com.example.pawpals.message.ChatMessage

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var userName: String
    private val currentUserId = "USER_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("receiverName")
            ?: intent.getStringExtra("userName")
                    ?: "Teman Baru"
        val dogName = intent.getStringExtra("dogName")

        // Toolbar
        binding.toolbarChat.title = userName
        binding.toolbarChat.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil chat lama dari storage
        val messages = ChatStorage.getMessages(userName)

        // Kalau baru pertama kali buka & datang dari halaman adoption → buat auto message
        if (messages.isEmpty() && !dogName.isNullOrEmpty()) {
            messages.add(
                ChatMessage(
                    senderId = currentUserId,
                    message = "Hai! Aku tertarik adopsi $dogName, masih tersedia kah?",
                    timestamp = System.currentTimeMillis()
                )
            )
        } else if (messages.isEmpty()) {
            // Kalau bukan dari adoption, buat dummy chat awal
            messages.addAll(
                listOf(
                    ChatMessage("USER_2", "Hai! Kamu tertarik adopsi anjingku?", System.currentTimeMillis()),
                    ChatMessage("USER_1", "Iya, aku liat di halaman adoption, lucu banget 😍", System.currentTimeMillis()),
                    ChatMessage("USER_2", "Hehe iya, dia jinak banget!", System.currentTimeMillis())
                )
            )
        }

        // Setup RecyclerView
        adapter = ChatAdapter(messages, currentUserId)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter
        binding.rvChat.scrollToPosition(messages.size - 1)

        // Tombol kirim
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val newMessage = ChatMessage(currentUserId, text, System.currentTimeMillis())
                ChatStorage.addMessage(userName, newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                binding.rvChat.scrollToPosition(messages.size - 1)
                binding.etMessage.text.clear()
            }
        }
    }
}
