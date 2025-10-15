package com.example.pawpals.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pawpals.data.Event
import com.example.pawpals.data.EventRepository

class EventViewModel : ViewModel() {

    val events: LiveData<List<Event>> = EventRepository.events

    fun deleteEvent(eventId: Int) = EventRepository.deleteEvent(eventId)
    fun joinEvent(eventId: Int) = EventRepository.joinEvent(eventId)
    fun cancelJoin(eventId: Int) = EventRepository.cancelJoin(eventId)
    fun getEventById(id: Int): Event? = EventRepository.getEventById(id)
    fun refreshEvents() = EventRepository.refreshEvents()
    fun addEvent(title: String, description: String, date: String, location: String) =
        EventRepository.addEvent(title, description, date, location)
}
