package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeRepository
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var remindersRepository: FakeRepository
    private lateinit var app: Application

    @get:Rule var mainCoroutineRule = MainCoroutineRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        remindersRepository = FakeRepository()
        app = ApplicationProvider.getApplicationContext()
        saveReminderViewModel = SaveReminderViewModel(app, remindersRepository)
    }

    @Test
    fun onClear_fieldsAreNull() {
        saveReminderViewModel.reminderTitle.value = "TITLE"
        saveReminderViewModel.reminderDescription.value = "DESCRIPTION"
        saveReminderViewModel.reminderSelectedLocationStr.value = "LOCATION"
        saveReminderViewModel.selectedPOI.value = PointOfInterest(LatLng(1.111, 2.222), "PLACEID", "NAME")
        saveReminderViewModel.latitude.value = 1.111
        saveReminderViewModel.longitude.value = 2.222

        saveReminderViewModel.onClear()

        assertThat(saveReminderViewModel.reminderTitle.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.reminderDescription.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, isEmptyOrNullString())
        assertThat(saveReminderViewModel.selectedPOI.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.value, `is`(nullValue()))
    }

    @Test
    fun saveReminder_togglesLoadingAndSavesReminder() {
        mainCoroutineRule.pauseDispatcher()

        val title       = "TITLE"
        val description = "DESCRIPTION"
        val location    = "LOCATION"
        val latitude    = 1.111
        val longitude   = 2.222
        val id          = "UUID"

        val newReminder = ReminderDataItem(title, description, location, latitude, longitude, id)
        saveReminderViewModel.saveReminder(newReminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`(app.getString(R.string.reminder_saved)))

        runBlocking {
            val retrievedReminder = remindersRepository.getReminder(id) as Result.Success

            assertThat(retrievedReminder.data.title      , `is`(title      ))
            assertThat(retrievedReminder.data.description, `is`(description))
            assertThat(retrievedReminder.data.location   , `is`(location   ))
            assertThat(retrievedReminder.data.latitude   , `is`(latitude   ))
            assertThat(retrievedReminder.data.longitude  , `is`(longitude  ))
        }
    }

}