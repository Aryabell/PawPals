package com.example.pawpals.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pawpals.databinding.FragmentMessageListBinding
import com.example.pawpals.R

class MessageListFragment : Fragment() {

    private var _binding: FragmentMessageListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageListBinding.inflate(inflater, container, false)

        val dummyChats = listOf(
            ChatPreview("1", "Nadhira", "Kapan bisa ketemuan buat adopsi?", R.drawable.ava_user1, System.currentTimeMillis()),
            ChatPreview("2", "yeonjun", "Makasih ya infonya!", R.drawable.ava_user2, System.currentTimeMillis() - 3600000)
        )

        val adapter = MessageListAdapter(dummyChats) { chat ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("userName", chat.userName)
            startActivity(intent)
        }

        binding.rvMessageList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessageList.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
