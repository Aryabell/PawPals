package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.AdminActivity
import com.example.pawpals.MainActivity
import com.example.pawpals.databinding.ActivityLoginBinding
import com.example.pawpals.data.MemberRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.pawpals.model.Member

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val member = MemberRepository.validateLogin(email, password)

                if (member != null) {
                    if (member.blocked) {
                        Toast.makeText(this@LoginActivity, "Akun diblokir!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    Toast.makeText(this@LoginActivity, "Login sebagai ${member.role}", Toast.LENGTH_SHORT).show()

                    if (member.role.equals("Pengurus", ignoreCase = true)) {
                        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }

                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Email atau password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}