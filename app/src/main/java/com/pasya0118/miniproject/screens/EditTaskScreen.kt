package com.pasya0118.miniproject.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pasya0118.miniproject.R
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: String,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val task = viewModel.getTaskById(taskId).observeAsState().value

    if (task == null) {
        Text("Loading or Task not found...")
        return
    }

    var taskName by remember { mutableStateOf(task.name) }
    var taskDescription by remember { mutableStateOf(task.description) }
    var selectedPriority by remember { mutableStateOf(task.priority ?: Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(task.category ?: Category.PERSONAL) }
    var isImportantTask by remember { mutableStateOf(task.isCompleted.not()) }

    var isTaskNameError by remember { mutableStateOf(false) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val errorEmptyTaskMessage = stringResource(id = R.string.error_empty_task)
    val taskUpdatedMessage = stringResource(id = R.string.task_updated)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        content = {
                            Text(
                                text = snackbarData.visuals.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.edit_task)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Name
            OutlinedTextField(
                value = taskName,
                onValueChange = {
                    taskName = it
                    isTaskNameError = false
                },
                label = { Text(stringResource(id = R.string.task_name)) },
                isError = isTaskNameError,
                supportingText = {
                    if (isTaskNameError) {
                        Text(
                            text = stringResource(id = R.string.error_empty_task),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Task Description
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(id = R.string.task_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Task Pending Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isImportantTask,
                    onCheckedChange = { isImportantTask = it }
                )
                Text(text = "Task Pending")
            }

            // Priority Radio Buttons
            Text(
                text = stringResource(id = R.string.priority),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Priority.values().forEach { priority ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority }
                        )

                        Text(
                            text = priority.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Category Dropdown
            Text(
                text = stringResource(id = R.string.category),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
                ) {
                    Category.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.name) },
                            onClick = {
                                selectedCategory = category
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (taskName.isBlank()) {
                        isTaskNameError = true
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorEmptyTaskMessage,
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        viewModel.updateTask(
                            taskId,
                            taskName,
                            taskDescription,
                            selectedPriority.name,
                            selectedCategory.name
                        )

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = taskUpdatedMessage,
                                duration = SnackbarDuration.Short
                            )
                        }

                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
