package com.example.pawpals.admin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.model.Member
import com.example.pawpals.adapter.MemberAdapter
import com.example.pawpals.data.MemberRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MemberFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private val memberList = mutableListOf<Member>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_member, container, false)

        recyclerView = v.findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MemberAdapter(memberList) { showUserOptions(it) }
        recyclerView.adapter = adapter

        v.findViewById<FloatingActionButton>(R.id.fabAddUser)
            .setOnClickListener { createUser() }

        loadMembers()

        return v
    }

    private fun loadMembers() {
        lifecycleScope.launch {
            val allMembers = MemberRepository.getAllMembers()
            memberList.clear()
            memberList.addAll(allMembers)
            adapter.notifyDataSetChanged()
        }
    }

    private fun createUser() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputName = EditText(requireContext()).apply { hint = "Nama" }
        val inputEmail = EditText(requireContext()).apply { hint = "Email" }
        val inputPassword = EditText(requireContext()).apply { hint = "Password" }

        val roleOptions = arrayOf("Member", "Pengurus")
        val inputRole = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roleOptions)
        }

        layout.apply {
            addView(inputName)
            addView(inputEmail)
            addView(inputPassword)
            addView(inputRole)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Member Baru")
            .setView(layout)
            .setPositiveButton("Tambah") { _, _ ->
                lifecycleScope.launch {
                    val name = inputName.text.toString()
                    val email = inputEmail.text.toString()
                    val password = inputPassword.text.toString()
                    val role = inputRole.selectedItem.toString()

                    if (MemberRepository.findMemberByEmail(email) != null) {
                        Toast.makeText(requireContext(), "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    MemberRepository.addMember(Member(name, email, password, role, false))
                    loadMembers()

                    Toast.makeText(requireContext(), "$name ditambahkan sebagai $role", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
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

    private fun toggleRole(user: Member) {
        lifecycleScope.launch {
            val newRole = if (user.role == "Member") "Pengurus" else "Member"
            MemberRepository.updateRole(user.id!!, newRole)
            loadMembers()
        }
    }

    private fun deleteUser(user: Member) {
        lifecycleScope.launch {
            MemberRepository.removeMember(user.id!!)
            loadMembers()
        }
    }

    private fun blockUser(user: Member) {
        lifecycleScope.launch {
            MemberRepository.blockMember(user.id!!)
            loadMembers()
        }
    }
}
