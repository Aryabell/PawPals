package com.example.pawpals.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pawpals.api.ApiResponse
import com.example.pawpals.api.EventApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object EventRepository {

    // ==============================
    // RETROFIT
    // ==============================
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/pawpals_api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(EventApiService::class.java)

    // ==============================
    // LIVE DATA
    // ==============================
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    // ==============================
    // FETCH EVENTS
    // ==============================
    fun fetchEvents() {
        api.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(
                call: Call<List<Event>>,
                response: Response<List<Event>>
            ) {
                if (response.isSuccessful) {
                    _events.value = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {}
        })
    }

    // ==============================
    // ADD EVENT WITH IMAGE (ADMIN)
    // ==============================
    fun addEventWithImage(
        context: Context,
        title: String,
        description: String,
        date: String,
        location: String,
        imageUri: Uri?
    ) {
        val titleBody = title.toRequestBody("text/plain".toMediaType())
        val descBody = description.toRequestBody("text/plain".toMediaType())
        val dateBody = date.toRequestBody("text/plain".toMediaType())
        val locBody = location.toRequestBody("text/plain".toMediaType())

        var imagePart: MultipartBody.Part? = null

        if (imageUri != null) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val reqFile = bytes.toRequestBody("image/jpeg".toMediaType())
                imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "event_${System.currentTimeMillis()}.jpg",
                    reqFile
                )
            }
        }

        api.addEventWithImage(
            titleBody,
            descBody,
            dateBody,
            locBody,
            imagePart
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                fetchEvents()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    // ==============================
    // DELETE EVENT
    // ==============================
    fun deleteEvent(eventId: Int) {
        api.deleteEvent(eventId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                fetchEvents()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }

    // ==============================
    // JOIN / CANCEL
    // ==============================
    fun joinEvent(eventId: Int) {
        api.joinEvent(eventId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                fetchEvents() // 🔥 INI WAJIB
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }

    fun cancelJoin(eventId: Int) {
        api.cancelJoin(eventId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                fetchEvents()
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }

    fun getEventById(id: Int): Event? =
        _events.value?.find { it.id == id }

    fun refreshEvents() {
        fetchEvents()
    }
}
