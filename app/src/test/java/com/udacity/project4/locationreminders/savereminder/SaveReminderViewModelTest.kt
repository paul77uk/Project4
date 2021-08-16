package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
// Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun testData_isNullOrEmpty() {

        val reminderList = listOf(
            ReminderDataItem("", "Description", "", 0.0, 0.0),
            ReminderDataItem("title", "Description", "", 0.0, 0.0),
            ReminderDataItem("", "Description", "location", 0.0, 0.0),
        )

        var value = false

        for (data in reminderList) {
            if (data.title.isNullOrEmpty() || data.location.isNullOrEmpty()) {
                value = true
                break
            }
        }

        assertThat(value, `is`(true))

    }

    @Test
    fun onClear_returnsNull() = runBlockingTest {

        val liveData = listOf(
            saveReminderViewModel.reminderTitle.value,
            saveReminderViewModel.reminderDescription.value,
            saveReminderViewModel.reminderSelectedLocationStr.value,
            saveReminderViewModel.selectedPOI.value,
            saveReminderViewModel.latitude.value,
            saveReminderViewModel.longitude.value
        )

        saveReminderViewModel.onClear()

        var nullCount = 0

        for (i in liveData) if (i == null) nullCount++

        val value = nullCount == liveData.size

        assertThat(value, Matchers.`is`(true))

    }

    @Test
    fun validateEnteredData_titleOrLocationIsEmptyOrNull_returnsTrue() = runBlockingTest {

        val enteredData = listOf(
            ReminderDataItem("", "Description", "", 0.0, 0.0),
            ReminderDataItem("title", "Description", "", 0.0, 0.0),
            ReminderDataItem("", "Description", "location", 0.0, 0.0),
            ReminderDataItem(null, "Description", "location", 0.0, 0.0),
            ReminderDataItem("title", "Description", null, 0.0, 0.0),
            ReminderDataItem(null, "Description", null, 0.0, 0.0),
        )

        var nullANdEmptyCount = 0

        // if title pr location is nullOrEmpty increment count
        for (data in enteredData) {
            if (!saveReminderViewModel.validateEnteredData(data)) nullANdEmptyCount++

        }

        // if all in enteredData list are empty or null as they should be in these tests, then value = true
        val value = nullANdEmptyCount == enteredData.size

        assertThat(value, `is`(true))

    }

    @Test
    fun validateEnteredData_showSnackBarIntValue_returnsTrue() = runBlockingTest {

        val enteredData = listOf(
            ReminderDataItem("", "Description", "location", 0.0, 0.0),
            ReminderDataItem("title", "Description", "", 0.0, 0.0),
            ReminderDataItem(null, "Description", "location", 0.0, 0.0),
            ReminderDataItem("title", "Description", null, 0.0, 0.0),
            ReminderDataItem(null, "Description", "location", 0.0, 0.0),
        )

        val snackBarList = mutableListOf<Int>()

        // if title pr location is nullOrEmpty increment count
        for (data in enteredData) {
            saveReminderViewModel.validateEnteredData(data)
            saveReminderViewModel.showSnackBarInt.value?.let { snackBarList.add(it) }

        }

        // if all in enteredData list are empty or null as they should be in these tests, then value = true
        val expected = listOf(
            R.string.err_enter_title,
            R.string.err_select_location,
            R.string.err_enter_title,
            R.string.err_select_location,
            R.string.err_enter_title
        )

        val value = expected == snackBarList

        assertThat(value, `is`(true))

    }

    @Test
    fun saveReminder_showLoading() = runBlockingTest {

        val data = ReminderDataItem("title", "desc", "loc", 0.0, 0.0)

        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(data)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun saveReminder_showToastValue_returnsTrue() = runBlockingTest{

        val reminder = ReminderDataItem(
            "test",
            "desc",
            "loc",
            1.0,
            2.0
        )

        saveReminderViewModel.saveReminder(reminder)

        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`(
            getApplicationContext<Context>()
                .getString(R.string.reminder_saved))
        )

    }


}