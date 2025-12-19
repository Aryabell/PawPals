package com.example.pawpals.api

import android.content.Context
import android.content.SharedPreferences

class ChatSessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "pawpals_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGIN = "is_login"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = prefs.edit()

    // simpan session setelah login
    fun saveLoginSession(
        userId: Int,
        username: String
    ) {
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.apply()
    }

    // ambil userId
    val userId: Int
        get() = prefs.getInt(KEY_USER_ID, 0)

    // ambil username
    val username: String?
        get() = prefs.getString(KEY_USERNAME, null)

    // cek login
    val isLogin: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGIN, false)

    // logout
    fun logout() {
        editor.clear()
        editor.apply()
    }
}
