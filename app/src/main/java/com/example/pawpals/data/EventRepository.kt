package com.example.pawpals.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pawpals.api.DogApiService
import com.example.pawpals.api.DogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object EventRepository {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val dogApi = retrofit.create(DogApiService::class.java)

    init {
        val initialEvents = listOf(
            Event(1, "Dog Morning Walk", "Jalan santai pagi bareng doggos.", "2025-10-01 07:00", "Taman Merdeka"),
            Event(2, "Playdate at Park", "Bawa mainan & snack, social time.", "2025-10-03 16:00", "Taman Bunga"),
            Event(3, "Puppy Socialization", "Sesi puppy + grooming tips.", "2025-10-05 10:00", "Community Hall")
        )
        _events.value = initialEvents
        fetchDogImages()
    }

    private fun fetchDogImages() {
        _events.value?.forEach { event ->
            dogApi.getRandomDog().enqueue(object : Callback<DogResponse> {
                override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                    val url = response.body()?.message
                    _events.postValue(
                        _events.value?.map {
                            if (it.id == event.id) it.copy(imageUrl = url) else it
                        }
                    )
                }

                override fun onFailure(call: Call<DogResponse>, t: Throwable) {}
            })
        }
    }

    fun deleteEvent(eventId: Int) {
        val currentList = _events.value?.toMutableList() ?: return
        val newList = currentList.filterNot { it.id == eventId }
        _events.value = newList
    }

    fun addEvent(title: String, description: String, date: String, location: String) {
        val currentList = _events.value?.toMutableList() ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1

        val newEvent = Event(
            id = newId,
            title = title,
            description = description,
            date = date,
            location = location
        )

        currentList.add(newEvent)
        _events.value = currentList

        dogApi.getRandomDog().enqueue(object : Callback<DogResponse> {
            override fun onResponse(call: Call<DogResponse>, response: Response<DogResponse>) {
                val url = response.body()?.message
                _events.postValue(
                    _events.value?.map {
                        if (it.id == newId) it.copy(imageUrl = url) else it
                    }
                )
            }

            override fun onFailure(call: Call<DogResponse>, t: Throwable) {}
        })
    }


    fun joinEvent(eventId: Int) {
        _events.value = _events.value?.map { ev ->
            if (ev.id == eventId) ev.copy(isJoined = true) else ev
        }
    }

    fun cancelJoin(eventId: Int) {
        _events.value = _events.value?.map { ev ->
            if (ev.id == eventId) ev.copy(isJoined = false) else ev
        }
    }

    fun getEventById(id: Int): Event? = _events.value?.find { it.id == id }

    fun refreshEvents() {
        fetchDogImages()
    }
}
