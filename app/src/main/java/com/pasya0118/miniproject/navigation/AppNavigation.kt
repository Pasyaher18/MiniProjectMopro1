package com.pasya0118.miniproject.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.screens.AddTaskScreen
import com.pasya0118.miniproject.screens.TaskDetailScreen
import com.pasya0118.miniproject.screens.TaskListScreen
import com.pasya0118.miniproject.screens.TrashScreen
import com.pasya0118.miniproject.util.NoInternetScreen
import com.pasya0118.miniproject.util.isConnectedToInternet
import com.pasya0118.miniproject.viewmodel.TaskViewModel


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = com.pasya0118.miniproject.data.AppDatabase.getInstance(context)
    val factory = com.pasya0118.miniproject.viewmodel.TaskViewModelFactory(db.taskDao())
    val taskViewModel: TaskViewModel = viewModel(factory = factory)

    var selectedTask by remember { mutableStateOf<Task?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                navController = navController,
                viewModel = taskViewModel,
                onAddTask = {
                    selectedTask = null
                    navController.navigate(Screen.AddTask.route)
                },
                onTaskClick = { task ->
                    selectedTask = task
                    navController.navigate(Screen.TaskDetail.route)
                }
            )
        }

        composable(Screen.AddTask.route) {
            AddTaskScreen(
                viewModel = taskViewModel,
                task = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TaskDetail.route) {
            selectedTask?.let { task ->
                TaskDetailScreen(
                    task = task,
                    viewModel = taskViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    navController = navController
                )
            }
        }

        composable(Screen.EditTask.route) {
            AddTaskScreen(
                viewModel = taskViewModel,
                task = selectedTask,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Trash.route) {
            TrashScreen(
                navController = navController,
                viewModel = taskViewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AppEntryScreen() {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isConnectedToInternet(context)) }

    if (!isConnected) {
        NoInternetScreen(onRetry = {
            isConnected = isConnectedToInternet(context)
        })
    } else {
        AppNavigation()
    }
}


sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")

    object TaskDetail : Screen("task_detail") {
        fun withId(taskId: String): String {
            return "task_detail/$taskId"
        }
    }

    object EditTask : Screen("edit_task") {
        fun withId(taskId: String): String {
            return "edit_task/$taskId"
        }
    }
    object Trash : Screen("trash")
}