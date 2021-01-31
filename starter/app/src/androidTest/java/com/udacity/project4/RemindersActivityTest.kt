package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.RemindersDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalDataSource
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest : AutoCloseKoinTest() {

    private lateinit var dataSource: RemindersDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initKoin() {
        stopKoin()

        appContext = getApplicationContext()

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

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @After
    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }

    @Test
    fun login(): Unit {
        runBlocking {
            val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
            dataBindingIdlingResource.monitorActivity(activityScenario)

            // activity_authentication
            onView(withId(R.id.login_button)).perform(click())

            // firebase sign in ui
            onView(withText(R.string.fui_sign_in_with_email)).perform(click())
            onView(withId(R.id.email)).perform(typeText("example@meow.com"), closeSoftKeyboard())
            onView(withId(R.id.button_next)).perform(click())
            onView(withId(R.id.password)).perform(typeText("M!1kshake"), closeSoftKeyboard())
            onView(withId(R.id.button_done)).perform(click())

            onView(withId(R.id.noDataTextView)).check(matches(withText(appContext.getString(R.string.no_data))))
        }
    }

}
