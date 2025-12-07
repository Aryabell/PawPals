package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawpals.admin.LoginActivity
import com.example.pawpals.api.ApiClient
import com.example.pawpals.data.MemberRepository
import com.example.pawpals.databinding.ActivityRegisterBinding
import com.example.pawpals.model.Member
import com.google.gson.JsonObject
import retrofit2.Call

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

            ApiClient.instance.register(name, email, password)
                .enqueue(object : retrofit2.Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: retrofit2.Response<JsonObject>) {
                        if (response.isSuccessful && response.body()!=null) {
                            val body = response.body()!!
                            val status = body.get("status").asString
                            when (status) {
                                "success" -> {
                                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                    finish()
                                }
                                "exists" -> Toast.makeText(this@RegisterActivity, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(this@RegisterActivity, "Gagal register", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, "Response error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "Gagal koneksi: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        b.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

