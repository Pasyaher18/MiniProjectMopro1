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

    // Daftar task yang akan di-observe di UI
    val tasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // Menambahkan task baru ke database
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

    // Menghapus task berdasarkan ID
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
        }
    }

    // Toggle status completed task
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(isCompleted = !it.isCompleted)
                taskDao.updateTask(updatedTask)
            }
        }
    }

    // Menambahkan fungsi untuk mendapatkan task berdasarkan ID
    fun getTaskById(taskId: String): LiveData<Task> {
        return taskDao.getTaskByIdLive(taskId)
    }

    // Fungsi untuk memperbarui task
    fun updateTask(taskId: String, name: String, description: String, priority: String, category: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            task?.let {
                val updatedTask = it.copy(
                    name = name,
                    description = description,
                    priority = Priority.valueOf(priority), // Convert string to Priority enum
                    category = Category.valueOf(category)  // Convert string to Category enum
                )
                taskDao.updateTask(updatedTask)
            }
        }
    }
}
