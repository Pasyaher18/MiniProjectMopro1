package com.pasya0118.miniproject.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.screens.AddTaskScreen
import com.pasya0118.miniproject.screens.TaskDetailScreen
import com.pasya0118.miniproject.screens.TaskListScreen
import com.pasya0118.miniproject.screens.EditTaskScreen // Import EditTaskScreen
import com.pasya0118.miniproject.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel()

    var selectedTask by remember { mutableStateOf<Task?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        // Task List Screen
        composable(Screen.TaskList.route) {
            TaskListScreen(
                viewModel = taskViewModel,
                onAddTask = {
                    navController.navigate(Screen.AddTask.route)
                },
                onTaskClick = { task ->
                    selectedTask = task
                    // Navigate to TaskDetail or EditTask with taskId
                    navController.navigate("edit_task/${task.id}") // Navigate to EditTaskScreen
                }
            )
        }

        // Add Task Screen
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                viewModel = taskViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Task Detail Screen
        composable(Screen.TaskDetail.route) {
            selectedTask?.let { task ->
                TaskDetailScreen(
                    task = task,
                    viewModel = taskViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Edit Task Screen
        composable("edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            taskId?.let {
                EditTaskScreen(
                    taskId = it,
                    viewModel = taskViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Sealed class for different screens
sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object TaskDetail : Screen("task_detail")
    // Add route for EditTaskScreen
    object EditTask : Screen("edit_task/{taskId}")
}
