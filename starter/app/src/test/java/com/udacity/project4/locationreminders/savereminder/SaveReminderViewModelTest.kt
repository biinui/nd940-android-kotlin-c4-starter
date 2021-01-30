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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var remindersRepository: FakeRepository
    private lateinit var app: Application

    @get:Rule var mainCoroutineRule = MainCoroutineRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        remindersRepository = FakeRepository()
        app = ApplicationProvider.getApplicationContext()
        saveReminderViewModel = SaveReminderViewModel(app, remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun onClear_fieldsAreNull() {
        saveReminderViewModel.apply {
            reminderTitle.value = "TITLE"
            reminderDescription.value = "DESCRIPTION"
            reminderSelectedLocationStr.value = "LOCATION"
            selectedPOI.value = PointOfInterest(LatLng(1.111, 2.222), "PLACEID", "NAME")
            latitude.value = 1.111
            longitude.value = 2.222
        }

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

        val newReminder = ReminderDataItem(title, description, location, latitude, longitude, id)
        saveReminderViewModel.saveReminder(newReminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`(app.getString(R.string.reminder_saved)))

        runBlocking {
            val retrievedReminder = (remindersRepository.getReminder(id) as Result.Success).data

            assertThat(retrievedReminder.title      , `is`(title      ))
            assertThat(retrievedReminder.description, `is`(description))
            assertThat(retrievedReminder.location   , `is`(location   ))
            assertThat(retrievedReminder.latitude   , `is`(latitude   ))
            assertThat(retrievedReminder.longitude  , `is`(longitude  ))
        }
    }

    @Test
    fun validateEnteredData_titleIsEmpty() {
        val emptyTitle  = ""

        val noTitleReminder = ReminderDataItem(emptyTitle, description, location, latitude, longitude, id)
        saveReminderViewModel.validateEnteredData(noTitleReminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_locationIsNull() {
        val nullLocation = null

        val noTitleReminder = ReminderDataItem(title, description, nullLocation, latitude, longitude, id)
        saveReminderViewModel.validateEnteredData(noTitleReminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }

    companion object {
        const val title       = "TITLE"
        const val description = "DESCRIPTION"
        const val location    = "LOCATION"
        const val latitude    = 1.111
        const val longitude   = 2.222
        const val id          = "UUID"
    }

}