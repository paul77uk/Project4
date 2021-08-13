package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    //    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
//    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
//    private lateinit var database: RemindersDatabase

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

//    @Before
//    fun initDB() {
//
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            RemindersDatabase::class.java
//        ).allowMainThreadQueries().build()
//    }

//    @After
//    fun closeDB() {
//        database.close()
//    }

    @Before
    fun setup() {
        stopKoin()
        appContext = getApplicationContext()

        val testModules = module {

            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }

            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }

            // in-memory database dao
            single {
                Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    RemindersDatabase::class.java
                )
                    // disable the main thread query check for Room
                    .allowMainThreadQueries()
                    .build()
                    .reminderDao()
            }
        }

        startKoin {
            androidContext(appContext)
            modules(listOf(testModules))
        }

//        repository = get()
//
//        runBlocking {
//            repository.deleteAllReminders()
//        }
    }

    //navigation test
    @Test
    fun onFabClick_navigateToSaveReminder() {

        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.AppTheme
        )

        val navController = mock(NavController::class.java)

        scenario.onFragment {

            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun reminderListFragment_DisplayedInUi() = runBlockingTest {

        val remindersDao: RemindersDao = get()

        val reminder = ReminderDTO(
            "title",
            "desc",
            "loc",
            0.0, 0.0
        )


        remindersDao.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withText(reminder.title)).check(matches(isDisplayed()))
        onView(withText(reminder.description)).check(matches(isDisplayed()))
        onView(withText(reminder.location)).check(matches(isDisplayed()))

    }

    @Test
    fun deleteAllReminders_displayNoDataTextView() = runBlockingTest {
//
        val remindersDao: RemindersDao = get()

        val reminder = ReminderDTO(
            "title",
            "desc",
            "loc",
            0.0, 0.0
        )

        remindersDao.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        remindersDao.deleteAllReminders()

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

    }
}