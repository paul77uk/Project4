package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private var reminderDTO = mutableListOf<ReminderDTO>()

    var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError) return Result.Error("Exception")

        return Result.Success(ArrayList(reminderDTO))
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTO.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Error")
        }

        val reminder = reminderDTO.find { it.id == id }
        return if(reminder != null){
            Result.Success(reminder)
        }else {
            Result.Error("Error not find Reminder")
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDTO.clear()
    }

}