package com.example.pawpals.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpals.R
import com.example.pawpals.model.Member

class MemberAdapter(
    private val users: List<Member>,
    private val onClick: (Member) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.txtUserName)
        val role: TextView = v.findViewById(R.id.txtUserRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.role.text = if (user.blocked) "🚫 Diblokir" else user.role
        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount() = users.size
}
