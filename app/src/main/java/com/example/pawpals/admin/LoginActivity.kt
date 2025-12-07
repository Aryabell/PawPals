package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.AdminActivity
import com.example.pawpals.MainActivity
import com.example.pawpals.api.ApiClient
import com.example.pawpals.databinding.ActivityLoginBinding
import com.example.pawpals.data.MemberRepository
import com.example.pawpals.model.Member
import com.google.gson.JsonObject
import retrofit2.Call

// imports singkat dihilangkan untuk ringkas
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

            // Panggil API
            ApiClient.instance.login(email, password).enqueue(object : retrofit2.Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: retrofit2.Response<JsonObject>) {
                    if (response.isSuccessful && response.body()!=null) {
                        val body = response.body()!!
                        val status = body.get("status").asString
                        if (status == "success") {
                            val user = body.getAsJsonObject("user")
                            val blocked = user.get("blocked").asInt
                            val role = user.get("role").asString

                            if (blocked == 1) {
                                Toast.makeText(this@LoginActivity, "Akun diblokir!", Toast.LENGTH_SHORT).show()
                                return
                            }

                            Toast.makeText(this@LoginActivity, "Login berhasil sebagai $role", Toast.LENGTH_SHORT).show()
                            if (role.equals("Pengurus", ignoreCase = true)) {
                                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                            } else {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            }
                            finish()
                        } else {
                            val msg = if (body.has("message")) body.get("message").asString else "Login gagal"
                            Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Response error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Gagal koneksi: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

