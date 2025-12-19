package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.MainActivity
import com.example.pawpals.admin.AdminActivity
import com.example.pawpals.api.ApiClient
import com.example.pawpals.api.ChatSessionManager
import com.example.pawpals.databinding.ActivityLoginBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            ApiClient.instance.login(email, password)
                .enqueue(object : Callback<JsonObject> {

                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (!response.isSuccessful || response.body() == null) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Response error",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        val body = response.body()!!
                        val status = body.get("status").asString

                        if (status != "success") {
                            val msg = if (body.has("message"))
                                body.get("message").asString
                            else "Login gagal"

                            Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                            return
                        }

                        val user = body.getAsJsonObject("user")
                        val blocked = user.get("blocked").asInt

                        if (blocked == 1) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Akun diblokir!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        // =========================
                        // DATA USER
                        // =========================
                        val userId = user.get("id").asInt.toString()
                        val username = user.get("name").asString
                        val emailUser = user.get("email").asString
                        val role = user.get("role").asString

                        // =========================
                        // 1️⃣ SIMPAN SESSION LAMA
                        // =========================
                        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                        prefs.edit()
                            .putString("user_id", userId)
                            .putString("username", username)
                            .putString("email", emailUser)
                            .putString("role", role)
                            .apply()

                        // =========================
                        // 2️⃣ SIMPAN SESSION CHAT
                        // =========================
                        val chatSession = ChatSessionManager(this@LoginActivity)
                        chatSession.saveLoginSession(
                            userId = userId.toInt(),
                            username = username
                        )

                        // =========================
                        // DEBUG (boleh hapus nanti)
                        // =========================
                        println("DEBUG LOGIN:")
                        println("chat isLogin = ${chatSession.isLogin}")
                        println("chat userId = ${chatSession.userId}")

                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil sebagai $role",
                            Toast.LENGTH_SHORT
                        ).show()

                        // =========================
                        // REDIRECT
                        // =========================
                        if (role.equals("Pengurus", ignoreCase = true)) {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    AdminActivity::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                        }
                        finish()
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Gagal koneksi: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
