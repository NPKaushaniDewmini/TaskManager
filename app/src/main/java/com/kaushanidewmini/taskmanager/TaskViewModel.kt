package com.kaushanidewmini.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val db: TaskDatabaseHelper) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskDAO>>(emptyList())
    val tasks: StateFlow<List<TaskDAO>> = _tasks.asStateFlow()

    fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskList = db.getAllTasks()
            _tasks.value = taskList
        }
    }

    fun createTask(task: TaskDAO) {
        viewModelScope.launch(Dispatchers.IO) {
            db.createTask(task)
            fetchTasks()  // Refresh the task list
        }
    }

    fun updateTask(task: TaskDAO) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateTask(task)
            fetchTasks()  // Refresh the task list
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(taskId)
            fetchTasks()  // Refresh the task list
        }
    }

    fun getTaskByID(taskId: Int): Flow<TaskDAO?> {
        return flow {
            val task = db.getTaskByID(taskId)
            emit(task)
        }.flowOn(Dispatchers.IO)
    }
}
