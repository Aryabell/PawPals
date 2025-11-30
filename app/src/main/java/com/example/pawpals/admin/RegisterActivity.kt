package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.LoginActivity
import com.example.pawpals.data.MemberRepository
import com.example.pawpals.databinding.ActivityRegisterBinding
import com.example.pawpals.model.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnRegister.setOnClickListener {

            val name = b.edtName.text.toString().trim()
            val email = b.edtEmail.text.toString().trim()
            val password = b.edtPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {

                val existingMember = MemberRepository.findMemberByEmail(email)
                if (existingMember != null) {
                    Toast.makeText(this@RegisterActivity, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val newMember = Member(name, email, password, "Member", false)
                MemberRepository.addMember(newMember)

                Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }

        b.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
