package com.pasya0118.miniproject.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasya0118.miniproject.data.TaskDao
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.network.ApiClient
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks

    private val _trashTasks = mutableStateListOf<Task>()
    val trashTasks: List<Task> = _trashTasks

    init {
        loadTaskFromDb()
    }

    private fun loadTaskFromDb() {
        viewModelScope.launch {
            val active = taskDao.getAllActiveTasks()
            _tasks.clear()
            _tasks.addAll(active)

            val deleted = taskDao.getDeletedTasks()
            _trashTasks.clear()
            _trashTasks.addAll(deleted)
        }
    }

    fun addTask(name: String, description: String, priority: Priority, category: Category) {
        val newTask = Task(
            name = name,
            description = description,
            priority = priority,
            category = category
        )
        viewModelScope.launch {
            taskDao.insertTask(newTask)
            _tasks.add(newTask)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val deletedTask = task.copy(isDeleted = true)
                taskDao.updateTask(deletedTask)

                _tasks.removeIf { it.id == taskId }
                _trashTasks.add(deletedTask)
            }
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = _tasks[index]
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            viewModelScope.launch {
                taskDao.updateTask(updatedTask)
                _tasks[index] = updatedTask
            }
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            taskDao.updateTask(updatedTask)
            val index = _tasks.indexOfFirst { it.id == updatedTask.id }
            if (index != -1) {
                _tasks[index] = updatedTask
            }
        }
    }
    fun restoreTask(task: Task) {
        val restoredTask = task.copy(isDeleted = false)
        viewModelScope.launch {
            taskDao.updateTask(restoredTask)
            _trashTasks.remove(task)
            _tasks.add(restoredTask)
        }
    }

    fun permanentlyDeleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            _trashTasks.remove(task)
        }
    }
    fun fetchTasksFromApi() {
        viewModelScope.launch {
            try {
                val remoteTasks = ApiClient.taskApiService.getTasks()

                Log.d("TaskViewModel", "API Success, total: ${remoteTasks.size}")
                remoteTasks.forEachIndexed { index, task ->
                    Log.d(
                        "TaskViewModel",
                        "[$index] id=${task}, name=${task.name}, description=${task.description}, priority=${task.priority}, category=${task.category}"
                    )
                }

                _tasks.clear()

                val mappedTasks = remoteTasks.map {
                    Task(
                        name = it.name,
                        description = it.description,
                        priority = Priority.valueOf(it.priority.uppercase()), // "LOW" -> Priority.LOW
                        category = Category.valueOf(it.category.uppercase()), // "WORK" -> Category.WORK
                        isDeleted = false,
                        isCompleted = false
                    )
                }

                _tasks.addAll(mappedTasks)

                Log.d("TaskViewModel", "Mapped ${mappedTasks.size} tasks ke _tasks")

            } catch (e: Exception) {
                Log.e("TaskViewModel", "Gagal ambil data dari API: ${e.message}", e)
            }
        }
    }

}
