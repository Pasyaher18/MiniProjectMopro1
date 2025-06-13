package com.pasya0118.miniproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pasya0118.miniproject.model.Task
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskByIdLive(taskId: String): LiveData<Task>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): Task?
}
