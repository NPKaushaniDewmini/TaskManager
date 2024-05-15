package com.kaushanidewmini.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
//create view model class
class TaskViewModelFactory(private val db: TaskDatabaseHelper) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is TaskViewModel or its subclass
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            // If it is, create a new instance of TaskViewModel with the provided TaskDatabaseHelper
            return TaskViewModel(db) as T
        }
        // If the requested ViewModel class is not TaskViewModel, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}