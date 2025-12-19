package com.example.pawpals.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.pawpals.api.ChatSessionManager
import com.example.pawpals.api.MessageApiClient
import com.example.pawpals.databinding.FragmentMessageListBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageListFragment : Fragment() {

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!

    private val session by lazy {
        ChatSessionManager(requireContext())
    }

    private val chats = mutableListOf<ChatPreview>()
    private lateinit var adapter: MessageListAdapter

    // ===============================
    // LIFECYCLE
    // ===============================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessageListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupFab()
        setupSwipeRefresh()

        if (session.isLogin) {
            loadChats()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // auto refresh saat balik dari ChatActivity
        if (session.isLogin) {
            loadChats()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===============================
    // UI SETUP
    // ===============================
    private fun setupRecyclerView() {
        adapter = MessageListAdapter(chats) { chat ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("chatId", chat.chat_id)
            intent.putExtra("receiverId", chat.user_id)
            intent.putExtra("receiverName", chat.user_name)
            startActivity(intent)
        }

        binding.rvMessageList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessageList.adapter = adapter
    }

    private fun setupFab() {
        binding.fabNewChat.setOnClickListener {
            val sheet = NewChatBottomSheet(
                currentUserId = session.userId
            ) { chatId, receiverId, receiverName ->

                // refresh list setelah chat baru
                loadChats()

                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("chatId", chatId)
                intent.putExtra("receiverId", receiverId)
                intent.putExtra("receiverName", receiverName)
                startActivity(intent)
            }

            sheet.show(parentFragmentManager, "NewChatBottomSheet")
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadChats()
        }
    }

    // ===============================
    // API
    // ===============================
    private fun loadChats() {
        binding.swipeRefresh.isRefreshing = true

        MessageApiClient.api.getChatList(session.userId)
            .enqueue(object : Callback<List<ChatPreview>> {

                override fun onResponse(
                    call: Call<List<ChatPreview>>,
                    response: Response<List<ChatPreview>>
                ) {
                    chats.clear()
                    chats.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()

                    binding.swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<List<ChatPreview>>, t: Throwable) {
                    t.printStackTrace()
                    binding.swipeRefresh.isRefreshing = false
                }
            })
    }
}
