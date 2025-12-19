package com.example.pawpals.event

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.pawpals.data.Event
import com.example.pawpals.data.EventRepository

class EventViewModel : ViewModel() {

    val events: LiveData<List<Event>> = EventRepository.events
    fun fetchEvents() = EventRepository.fetchEvents()
    fun deleteEvent(eventId: Int) = EventRepository.deleteEvent(eventId)
    fun joinEvent(eventId: Int) = EventRepository.joinEvent(eventId)
    fun cancelJoin(eventId: Int) = EventRepository.cancelJoin(eventId)
    fun getEventById(id: Int): LiveData<Event?> {
        return EventRepository.events.map { list ->
            list.find { it.id == id }
        }
    }

    fun refreshEvents() = EventRepository.refreshEvents()
    fun addEvent(
        context: Context,
        title: String,
        description: String,
        date: String,
        location: String,
        imageUri: Uri?
    ) {
        EventRepository.addEventWithImage(
            context,
            title,
            description,
            date,
            location,
            imageUri
        )
    }
}
