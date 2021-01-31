package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private val reminder1 = ReminderDTO("TITLE1", "DESCRIPTION1", "LOCATION1", 1.111, 1.111, "UUID1")
    private val reminder2 = ReminderDTO("TITLE2", "DESCRIPTION2", "LOCATION2", 2.222, 2.222, "UUID2")

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        database.reminderDao().saveReminder(reminder1)

        val retrievedReminder = database.reminderDao().getReminderById(reminder1.id)

        assertThat(retrievedReminder as ReminderDTO, notNullValue())

        assertThat(retrievedReminder.id         , `is`(reminder1.id         ))
        assertThat(retrievedReminder.title      , `is`(reminder1.title      ))
        assertThat(retrievedReminder.description, `is`(reminder1.description))
        assertThat(retrievedReminder.location   , `is`(reminder1.location   ))
        assertThat(retrievedReminder.latitude   , `is`(reminder1.latitude   ))
        assertThat(retrievedReminder.longitude  , `is`(reminder1.longitude  ))
    }

    @Test
    fun saveRemindersAndGetReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        val retrievedReminders = database.reminderDao().getReminders()

        assertThat(retrievedReminders, hasSize<ReminderDTO>(2))
    }

    @Test
    fun deleteReminders_getNoReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        database.reminderDao().deleteAllReminders()
        val retrievedReminders = database.reminderDao().getReminders()

        assertThat(retrievedReminders, hasSize<ReminderDTO>(0))
    }


}