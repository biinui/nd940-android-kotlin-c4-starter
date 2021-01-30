package com.udacity.project4.locationreminders.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    private val reminder1 = ReminderDTO("TITLE1", "DESCRIPTION1", "LOCATION1", 1.111, 1.111, "UUID1")
    private val reminder2 = ReminderDTO("TITLE2", "DESCRIPTION2", "LOCATION2", 2.222, 2.222, "UUID2")
    private val reminders = listOf(reminder1, reminder2).sortedBy { it.id }

    private val newReminder = ReminderDTO("TITLENEW", "DESCRIPTIONNEW", "LOCATIONNEW", 3.333, 3.333, "UUIDNEW")

    private lateinit var dao: FakeDao

    private lateinit var remindersRepository: RemindersLocalRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        dao = FakeDao(reminders.toMutableList())
        remindersRepository = RemindersLocalRepository(dao, Dispatchers.Unconfined)
    }

    @Test
    fun getReminders_getsAllReminders() {
        runBlocking {
            val retrievedReminders = remindersRepository.getReminders() as Result.Success
        }
    }

    @Test
    fun testSaveReminder() {}

    @Test
    fun testGetReminder() {}

    @Test
    fun testDeleteAllReminders() {}

}