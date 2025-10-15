package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.AdminActivity
import com.example.pawpals.MainActivity
import com.example.pawpals.databinding.ActivityLoginBinding
import com.example.pawpals.data.MemberRepository
import com.example.pawpals.model.Member

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Tombol Login ditekan
        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek ke repository
            val member = MemberRepository.validateLogin(email, password)

            if (member != null) {
                if (member.blocked) {
                    Toast.makeText(this, "Akun diblokir!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Toast.makeText(this, "Login berhasil sebagai ${member.role}", Toast.LENGTH_SHORT).show()

                // Arahkan sesuai role
                if (member.role.equals("Pengurus", ignoreCase = true)) {
                    startActivity(Intent(this, AdminActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }

                finish() // tutup halaman login
            } else {
                Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show()
            }
            }
        b.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }
}
