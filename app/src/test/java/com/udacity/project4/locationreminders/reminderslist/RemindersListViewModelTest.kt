package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.ReminderDataSource
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    // Subject under test
//    private lateinit var remindersListViewModel: RemindersListViewModel


//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()

    //    @Before
//    fun setupViewModel() {
//    val remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext())
//    }

//    remindersListViewModel.loadReminders()

    @Test
    fun loadReminders_Test() {

        val remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext())

        remindersListViewModel.loadReminders()

//        val value = remindersListViewModel.remindersList.getOrAwaitValue()
//
//        assertThat(value, not(nullValue()))
    }

}