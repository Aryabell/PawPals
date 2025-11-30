package com.example.pawpals.data

import com.example.pawpals.model.Member
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object MemberRepository {

    private val db = FirebaseFirestore.getInstance()
    private val memberRef = db.collection("members")

    // ➤ Tambah member ke Firestore
    suspend fun addMember(member: Member) {
        memberRef.add(member).await()
    }

    // ➤ Hapus member berdasarkan document ID
    suspend fun removeMember(id: String) {
        memberRef.document(id).delete().await()
    }

    // ➤ Update role
    suspend fun updateRole(id: String, newRole: String) {
        memberRef.document(id).update("role", newRole).await()
    }

    // ➤ Blokir member
    suspend fun blockMember(id: String) {
        memberRef.document(id).update("blocked", true).await()
    }

    // ➤ Cari member berdasarkan email
    suspend fun findMemberByEmail(email: String): Member? {
        val result = memberRef.whereEqualTo("email", email).get().await()

        return if (!result.isEmpty) {
            val doc = result.documents[0]
            doc.toObject(Member::class.java)?.apply { this.id = doc.id }
        } else null
    }

    suspend fun getAllMembers(): List<Member> {
        val result = memberRef.get().await()
        return result.documents.mapNotNull { doc ->
            doc.toObject(Member::class.java)?.apply { id = doc.id }
        }
    }


    // ➤ Validasi login
    suspend fun validateLogin(email: String, password: String): Member? {
        val result = memberRef
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()

        return if (!result.isEmpty) {
            val doc = result.documents[0]
            doc.toObject(Member::class.java)?.apply { this.id = doc.id }
        } else null
    }
}
