package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalDataSourceTest {

    private val reminder1 = ReminderDTO("TITLE1", "DESCRIPTION1", "LOCATION1", 1.111, 1.111, "UUID1")
    private val reminder2 = ReminderDTO("TITLE2", "DESCRIPTION2", "LOCATION2", 2.222, 2.222, "UUID2")

    private lateinit var dataSource: RemindersLocalDataSource
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()

        dataSource = RemindersLocalDataSource(
                database.reminderDao(),
                Dispatchers.Main
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getReminders_retrievesReminders() = runBlocking {
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)

        val result = dataSource.getReminders()

        assertThat(result.succeeded, `is`(true))

        val retrievedReminder = (result as Result.Success).data
        assertThat(retrievedReminder, hasSize<ReminderDTO>(2))
    }

    @Test
    fun saveAndGetReminder_retrievesReminder() = runBlocking {
        dataSource.saveReminder(reminder1)

        val result = dataSource.getReminder(reminder1.id)

        assertThat(result.succeeded, `is`(true))

        val retrievedReminder = (result as Result.Success).data
        assertThat(retrievedReminder.title      , `is` ("TITLE1"        ))
        assertThat(retrievedReminder.description, `is` ("DESCRIPTION1"  ))
        assertThat(retrievedReminder.location   , `is` ("LOCATION1"     ))
        assertThat(retrievedReminder.longitude  , `is` (1.111           ))
        assertThat(retrievedReminder.latitude   , `is` (1.111           ))
    }

    @Test
    fun testDeleteAllReminders() = runBlocking {
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)

        dataSource.deleteAllReminders()
        val result = dataSource.getReminders()

        assertThat(result.succeeded, `is`(true))

        val retrievedReminder = (result as Result.Success).data
        assertThat(retrievedReminder, hasSize<ReminderDTO>(0))
    }

}