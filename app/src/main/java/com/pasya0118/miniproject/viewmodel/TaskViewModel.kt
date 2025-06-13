package com.pasya0118.miniproject.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.model.Task

class TaskViewModel : ViewModel() {

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks

    fun addTask(name: String, description: String, priority: Priority, category: Category) {
        val newTask = Task(
            name = name,
            description = description,
            priority = priority,
            category = category
        )
        _tasks.add(newTask)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteTask(taskId: String) {
        _tasks.removeIf { it.id == taskId }
    }

    fun toggleTaskCompletion(taskId: String) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = _tasks[index]
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
        }
    }

    fun getTaskById(taskId: String): Task? {
        return _tasks.find { it.id == taskId }
    }

    fun updateTask(updatedTask: Task) {
        val index = _tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            _tasks[index] = updatedTask
        }
    }
}
