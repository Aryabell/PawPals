package com.example.pawpals.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pawpals.MainActivity
import com.example.pawpals.R
import com.example.pawpals.api.ApiClient
import com.example.pawpals.model.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            // Sudah login, langsung ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val btnGoRegister: Button = findViewById(R.id.btnGoRegister)
        val edtEmail: EditText = findViewById(R.id.edtEmail)
        val edtPassword: EditText = findViewById(R.id.edtPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val profileImageView: ImageView = findViewById(R.id.profileImageView)

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = ApiClient.instance
            api.login(email, password).enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_SHORT).show()
                        if (body.success) {
                            // Simpan login status, role, dan user_id
                            prefs.edit().apply {
                                putBoolean("IS_LOGGED_IN", true)
                                putString("USER_ROLE", body.user?.role ?: "user")
                                putString("USER_ID", body.user?.id?.toString()) // simpan id user
                                apply()
                            }

                            Log.d("LoginActivity", "Role disimpan: ${body.user?.role}")
                            Log.d("LoginActivity", "UserID disimpan: ${body.user?.id}")


                            // Tampilkan profil jika ada
                            body.user?.profile_pic?.let { pic ->
                                Glide.with(this@LoginActivity)
                                    .load("http://10.0.2.2/myapp/uploads/$pic")
                                    .into(profileImageView)
                            }

                            // Pindah ke MainActivity
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
