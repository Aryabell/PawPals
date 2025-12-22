package com.example.pawpals.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject

import com.example.pawpals.api.ApiClient
import com.example.pawpals.api.MessageApiClient
import com.example.pawpals.databinding.BottomSheetMessageBinding
import com.example.pawpals.model.Member

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewChatBottomSheet(
    private val currentUserId: Int,
    private val onChatCreated: (chatId: Int, receiverId: Int, receiverName: String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMessageBinding

    private val members = mutableListOf<Member>()
    private var selectedMember: Member? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMessageBinding.inflate(inflater, container, false)

        loadMembers()

        binding.btnSendForm.setOnClickListener {
            val message = binding.etMessageForm.text.toString().trim()
            if (selectedMember == null || message.isEmpty()) return@setOnClickListener
            createChatAndSend(message)
        }

        return binding.root
    }


    // ===============================
    // LOAD MEMBER (PARSE JSON MANUAL)
    // ===============================
    private fun loadMembers() {
        ApiClient.instance.getMembers()
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    val body = response.body() ?: return
                    val dataArray = body.getAsJsonArray("members") ?: return

                    members.clear()

                    for (json in dataArray) {
                        val obj = json.asJsonObject
                        val member = Member(
                            id = obj.get("id").asInt,
                            name = obj.get("name").asString,
                            email = obj.get("email").asString,
                            role = obj.get("role").asString,
                            blocked = obj.get("blocked").asInt
                        )

                        if (member.id != currentUserId && member.blocked == 0) {
                            members.add(member)
                        }
                    }

                    // 1. Siapkan Adapter
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        members.map { it.name }
                    )

                    // 2. Pasang Adapter (Pake setAdapter, bukan .adapter =)
                    binding.spinnerUser.setAdapter(adapter)

                    // 3. Handle Klik Item (Pake setOnItemClickListener)
                    binding.spinnerUser.setOnItemClickListener { _, _, position, _ ->
                        selectedMember = members[position]

                        Log.d("ChatSheet", "Selected: ${selectedMember?.name}")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    // ===============================
    // CREATE CHAT + SEND MESSAGE
    // ===============================
    private fun createChatAndSend(message: String) {
        val receiver = selectedMember ?: return

        MessageApiClient.api.createOrGetChat(
            currentUserId,
            receiver.id
        ).enqueue(object : Callback<Map<String, Any>> {

            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                val body = response.body()

                val chatId = when (val value = body?.get("chat_id")) {
                    is Double -> value.toInt()
                    is Int -> value
                    is String -> value.toIntOrNull()
                    else -> null
                }

                if (chatId == null) {
                    Log.e("CHAT", "chat_id null or invalid: $body")
                    return
                }


                MessageApiClient.api.sendMessage(
                    chatId,
                    currentUserId,
                    message
                ).enqueue(object : Callback<Map<String, Any>> {

                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        onChatCreated(chatId, receiver.id, receiver.name)
                        dismiss()
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
