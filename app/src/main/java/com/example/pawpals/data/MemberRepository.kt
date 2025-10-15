package com.example.pawpals.data

import com.example.pawpals.model.Member

object MemberRepository {

    // Data awal (dummy)
    val members = mutableListOf(
        Member("Arya", "arya@gmail.com", "123456", "Member", false),
        Member("Admin", "admin@admin.com", "admin123", "Pengurus", false),
    )

    // Tambah member baru
    fun addMember(member: Member) {
        members.add(member)
    }

    // Hapus member
    fun removeMember(member: Member) {
        members.remove(member)
    }

    // Update role
    fun updateRole(member: Member, newRole: String) {
        member.role = newRole
    }

    // Blokir member
    fun blockMember(member: Member) {
        member.blocked = true
    }

    // Cari member berdasarkan nama
    fun findMemberByName(name: String): Member? {
        return members.find { it.name.equals(name, ignoreCase = true) }
    }

    // Cari member berdasarkan email
    fun findMemberByEmail(email: String): Member? {
        return members.find { it.email.equals(email, ignoreCase = true) }
    }

    // 🔐 Validasi login berdasarkan email & password
    fun validateLogin(email: String, password: String): Member? {
        return members.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }
}
