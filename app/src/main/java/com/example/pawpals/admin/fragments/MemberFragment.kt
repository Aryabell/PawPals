package com.example.pawpals.admin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.model.Member
import com.example.pawpals.adapter.MemberAdapter
import com.example.pawpals.api.ApiClient
import com.example.pawpals.data.MemberRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MemberFragment : Fragment() {
    private val memberList = mutableListOf<Member>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private lateinit var fabAddUser: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_member, container, false)
        recyclerView = v.findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MemberAdapter(memberList) { showUserOptions(it) }
        recyclerView.adapter = adapter

        fabAddUser = v.findViewById(R.id.fabAddUser)
        fabAddUser.setOnClickListener { createUser() }

        loadMembers()
        return v
    }

    private fun loadMembers() {
        ApiClient.instance.getMembers().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body()!=null) {
                    val body = response.body()!!
                    if (body.get("status").asString == "success") {
                        memberList.clear()
                        val arr = body.getAsJsonArray("members")
                        for (elem in arr) {
                            val obj = elem.asJsonObject
                            val m = Member(
                                id = obj.get("id").asInt,
                                name = obj.get("name").asString,
                                email = obj.get("email").asString,
                                role = obj.get("role").asString,
                                blocked = obj.get("blocked").asInt
                            )
                            memberList.add(m)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal memuat: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // createUser() -> sama seperti sebelumnya, tapi gunakan ApiClient.instance.addMember(...)
    private fun createUser() {
        // ... build dialog same as you had, but on positive:
        // ApiClient.instance.addMember(name,email,password,role).enqueue(...)
        // on success -> Toast + loadMembers()
    }

    private fun toggleRole(user: Member) {
        val newRole = if (user.role == "Member") "Pengurus" else "Member"
        ApiClient.instance.updateRole(user.id, newRole).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "${user.name} sekarang $newRole", Toast.LENGTH_SHORT).show()
                    loadMembers()
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
        })
    }

    private fun deleteUser(user: Member) {
        ApiClient.instance.deleteMember(user.id).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Toast.makeText(requireContext(), "${user.name} dihapus", Toast.LENGTH_SHORT).show()
                loadMembers()
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
        })
    }

    private fun blockUser(user: Member) {
        ApiClient.instance.blockMember(user.id).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Toast.makeText(requireContext(), "${user.name} diblokir", Toast.LENGTH_SHORT).show()
                loadMembers()
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
        })
    }

    private fun showUserOptions(user: Member) {
        val options = arrayOf("Promote/Demote", "Hapus", "Blokir")
        AlertDialog.Builder(requireContext())
            .setTitle("Kelola ${user.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> toggleRole(user)
                    1 -> deleteUser(user)
                    2 -> blockUser(user)
                }
            }.show()
    }
}