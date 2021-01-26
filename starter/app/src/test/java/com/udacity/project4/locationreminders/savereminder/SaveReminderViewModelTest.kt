package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun saveNewReminder_savesNewReminder() {
        // GIVEN a fresh SaveReminderViewModel
        val dataSource = FakeDataSource()
        val viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        // and a new Reminder
        val newReminder = ReminderDataItem("TITLE", "DESCRIPTION", "LOCATION", 1.111, 9.999, "UUID")

        // WHEN saving a new reminder
        viewModel.saveReminder(newReminder)

        // THEN the new reminder must be saved

    }

}