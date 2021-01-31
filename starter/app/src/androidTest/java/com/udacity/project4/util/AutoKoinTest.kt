package com.udacity.project4.util

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.RemindersDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalDataSource
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

abstract class AutoKoinTest : KoinTest {

    lateinit var dataSource: RemindersDataSource
    lateinit var appContext: Application

    @Before
    fun initKoin() {
        stopKoin()

        appContext = ApplicationProvider.getApplicationContext()

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                        appContext,
                        get() as RemindersDataSource
                )
            }
            single {
                SaveReminderViewModel(
                        appContext,
                        get() as RemindersDataSource
                )
            }
            single { RemindersLocalDataSource(get()) as RemindersDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }

        startKoin {
            modules(listOf(myModule))
        }

        dataSource = get()

        runBlocking {
            dataSource.deleteAllReminders()
        }
    }

    @After
    fun autoClose() {
        stopKoin()
    }

}