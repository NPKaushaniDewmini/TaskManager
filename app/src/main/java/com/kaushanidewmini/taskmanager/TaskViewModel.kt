package com.kaushanidewmini.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val db: TaskDatabaseHelper) : ViewModel() {
    // MutableStateFlow to hold the list of tasks
    private val _tasks = MutableStateFlow<List<TaskDAO>>(emptyList())
    // StateFlow to expose the list of tasks as read-only
    val tasks: StateFlow<List<TaskDAO>> = _tasks.asStateFlow()
    // Function to fetch all tasks from the database
    fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskList = db.getAllTasks()
            _tasks.value = taskList
        }
    }
    // Function to create a new task in the database
    fun createTask(task: TaskDAO) {
        viewModelScope.launch(Dispatchers.IO) {
            db.createTask(task)
            fetchTasks()  // Refresh the task list
        }
    }
    // Function to update an existing task in the database
    fun updateTask(task: TaskDAO) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateTask(task)
            fetchTasks()  // Refresh the task list
        }
    }
    // Function to delete a task from the database
    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(taskId)
            fetchTasks()  // Refresh the task list
        }
    }
    // Function to get a task by its ID from the database
    fun getTaskByID(taskId: Int): Flow<TaskDAO?> {
        return flow {
            val task = db.getTaskByID(taskId)
            emit(task)
        }.flowOn(Dispatchers.IO)
    }
}
