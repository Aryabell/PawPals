package com.example.pawpals.data

import com.example.pawpals.model.Member

object MemberRepository {


    private val members = mutableListOf<Member>()

    fun setMembers(list: List<Member>) {
        members.clear()
        members.addAll(list)
    }

    fun getMembers(): List<Member> = members

    fun getById(id: Int): Member? {
        return members.find { it.id == id }
    }
}
