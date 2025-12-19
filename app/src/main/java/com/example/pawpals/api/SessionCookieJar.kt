package com.example.pawpals.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object SessionCookieJar : CookieJar {

    private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }

    fun clear() {
        cookieStore.clear()
    }
}
