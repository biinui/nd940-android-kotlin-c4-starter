package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.INTENTIONAL_ERROR
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
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
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var remindersRepository: FakeDataSource

    @get:Rule var mainCoroutineRule = MainCoroutineRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        remindersRepository = FakeDataSource()
        val reminder1 = ReminderDTO("TITLE1", "DESCRIPTION1", "LOCATION1", 1.111, 1.111, "UUID1")
        val reminder2 = ReminderDTO("TITLE2", "DESCRIPTION2", "LOCATION2", 2.222, 2.222, "UUID2")
        runBlocking {
            remindersRepository.saveReminder(reminder1)
            remindersRepository.saveReminder(reminder2)
        }
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_loadingShowsAndDataLoads() {
        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue(), hasSize(2))
    }
    @Test
    fun loadReminders_error() {
        mainCoroutineRule.pauseDispatcher()

        remindersRepository.setReturnError(true)
        remindersListViewModel.loadReminders()

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.remindersList.value, `is`(nullValue()))
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`(INTENTIONAL_ERROR))
    }


    @Test
    fun loadReminders_showNoData() {
        runBlocking {
            remindersRepository.deleteAllReminders()
        }

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

}