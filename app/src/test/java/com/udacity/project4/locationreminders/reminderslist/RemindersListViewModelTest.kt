package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

//    remindersListViewModel.loadReminders()

    @Test
    fun loadReminders_isNotEmpty() = runBlockingTest {

        val reminders = ReminderDTO("title", "description", "location", 0.0, 0.0)

        dataSource.deleteAllReminders()
        dataSource.saveReminder(reminders)
        remindersListViewModel.loadReminders()

        // will be false as inserted data
        val value = remindersListViewModel.showNoData.value

        assertThat(value, `is`(false))

    }

    @Test
    fun loadReminders_isEmpty() = runBlockingTest {

        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.showNoData.value

        assertThat(value, `is`(true))

    }

    @Test
    fun shouldReturnError_returnException() = runBlockingTest {

        dataSource.deleteAllReminders()
        dataSource.shouldReturnError = true
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.showSnackBar.getOrAwaitValue() == "Exception"

        assertThat(value, `is`(true))

    }

}