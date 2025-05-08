package com.pasya0118.miniproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pasya0118.miniproject.database.TaskDatabase
import com.pasya0118.miniproject.database.TaskDao
import kotlinx.coroutines.launch
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Task

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()

    val tasks: LiveData<List<Task>> = taskDao.getAllTasks()


    fun addTask(name: String, description: String, priority: Priority, category: Category) {
        val newTask = Task(
            name = name,
            description = description,
            priority = priority,
            category = category
        )
        viewModelScope.launch {
            taskDao.insertTask(newTask)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(isCompleted = !it.isCompleted)
                taskDao.updateTask(updatedTask)
            }
        }
    }
}