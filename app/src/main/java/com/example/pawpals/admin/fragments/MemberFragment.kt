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
import com.example.pawpals.data.MemberRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MemberFragment : Fragment() {

    private val memberList = MemberRepository.members

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private lateinit var fabAddUser: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_member, container, false)

        recyclerView = v.findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MemberAdapter(memberList) { showUserOptions(it) }
        recyclerView.adapter = adapter

        fabAddUser = v.findViewById(R.id.fabAddUser)
        fabAddUser.setOnClickListener { createUser() }

        return v
    }

    private fun createUser() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        // Label + Input Nama
        val nameLabel = TextView(requireContext()).apply {
            text = "Nama:"
            textSize = 16f
            setPadding(0, 0, 0, 8)
        }
        val inputName = EditText(requireContext()).apply {
            hint = "Masukkan nama member"
        }

        // Label + Input Email
        val emailLabel = TextView(requireContext()).apply {
            text = "Email:"
            textSize = 16f
            setPadding(0, 20, 0, 8)
        }
        val inputEmail = EditText(requireContext()).apply {
            hint = "Masukkan email"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        // Label + Input Password
        val passwordLabel = TextView(requireContext()).apply {
            text = "Password:"
            textSize = 16f
            setPadding(0, 20, 0, 8)
        }
        val inputPassword = EditText(requireContext()).apply {
            hint = "Masukkan password"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        // Label + Role
        val roleLabel = TextView(requireContext()).apply {
            text = "Pilih Role:"
            textSize = 16f
            setPadding(0, 20, 0, 8)
        }
        val roleOptions = arrayOf("Member", "Pengurus")
        val inputRole = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                roleOptions
            )
        }

        // Tambahkan ke layout
        layout.addView(nameLabel)
        layout.addView(inputName)
        layout.addView(emailLabel)
        layout.addView(inputEmail)
        layout.addView(passwordLabel)
        layout.addView(inputPassword)
        layout.addView(roleLabel)
        layout.addView(inputRole)

        // Dialog Tambah Member
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Member Baru")
            .setView(layout)
            .setPositiveButton("Tambah") { _, _ ->
                val name = inputName.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val password = inputPassword.text.toString().trim()
                val role = inputRole.selectedItem.toString()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Cek email sudah ada atau belum
                if (MemberRepository.findMemberByEmail(email) != null) {
                    Toast.makeText(requireContext(), "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Tambah member baru
                val newUser = Member(name, email, password, role, false)
                MemberRepository.addMember(newUser)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "$name ditambahkan sebagai $role", Toast.LENGTH_SHORT).show()
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
        user.role = if (user.role == "Member") "Pengurus" else "Member"
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${user.name} sekarang ${user.role}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteUser(user: Member) {
        memberList.remove(user)
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${user.name} dihapus", Toast.LENGTH_SHORT).show()
    }

    private fun blockUser(user: Member) {
        user.blocked = true
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "${user.name} diblokir", Toast.LENGTH_SHORT).show()
    }
}
