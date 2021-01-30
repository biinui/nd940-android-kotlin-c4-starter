package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDao(var reminders: MutableList<ReminderDTO>? = mutableListOf())
    : RemindersDao {

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): List<ReminderDTO> {
        return reminders ?: emptyList()
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        return reminders?.firstOrNull { it.id == reminderId }?.let { it }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}