package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.LoginActivity
import com.example.pawpals.data.MemberRepository
import com.example.pawpals.databinding.ActivityRegisterBinding
import com.example.pawpals.model.Member

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Tombol Register
        b.btnRegister.setOnClickListener {
            val name = b.edtName.text.toString().trim()
            val email = b.edtEmail.text.toString().trim()
            val password = b.edtPassword.text.toString().trim()

            // Validasi input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah email sudah terdaftar
            val existingMember = MemberRepository.findMemberByEmail(email)
            if (existingMember != null) {
                Toast.makeText(this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat member baru dengan role default = Member
            val newMember = Member(name, email, password, "Member", false)
            MemberRepository.addMember(newMember)

            Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()

            // Pindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Tombol ke Login
        b.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
