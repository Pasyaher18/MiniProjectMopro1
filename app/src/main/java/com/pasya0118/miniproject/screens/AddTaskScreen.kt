package com.pasya0118.miniproject.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pasya0118.miniproject.R
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    task: Task? = null
) {
    var taskName by rememberSaveable { mutableStateOf(task?.name ?: "") }
    var taskDescription by rememberSaveable { mutableStateOf(task?.description ?: "") }
    var selectedPriority by rememberSaveable { mutableStateOf(task?.priority ?: Priority.MEDIUM) }
    var selectedCategory by rememberSaveable { mutableStateOf(task?.category ?: Category.PERSONAL) }


    var isImportantTask by rememberSaveable { mutableStateOf(task?.priority == Priority.HIGH) }
    var isTaskNameError by remember { mutableStateOf(false) }

    var isCategoryExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val errorEmptyTaskMessage = stringResource(id = R.string.error_empty_task)
    val taskAddedMessage = stringResource(id = R.string.task_added)

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
                title = { Text(stringResource(id = R.string.add_task)) },
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
            // Nama Tugas
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

            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(id = R.string.task_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isImportantTask,
                    onCheckedChange = { isImportantTask = it }
                )
                Text(text = "Task Penting")
            }

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
                Priority.entries.forEach { priority ->
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

                        val priorityText = when (priority) {
                            Priority.HIGH -> stringResource(id = R.string.priority_high)
                            Priority.MEDIUM -> stringResource(id = R.string.priority_medium)
                            Priority.LOW -> stringResource(id = R.string.priority_low)
                        }

                        Text(
                            text = priorityText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

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
                    value = when (selectedCategory) {
                        Category.WORK -> stringResource(id = R.string.category_work)
                        Category.PERSONAL -> stringResource(id = R.string.category_personal)
                        Category.SHOPPING -> stringResource(id = R.string.category_shopping)
                        Category.OTHER -> stringResource(id = R.string.category_other)
                    },
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
                    Category.entries.forEach { category ->
                        val categoryText = when (category) {
                            Category.WORK -> stringResource(id = R.string.category_work)
                            Category.PERSONAL -> stringResource(id = R.string.category_personal)
                            Category.SHOPPING -> stringResource(id = R.string.category_shopping)
                            Category.OTHER -> stringResource(id = R.string.category_other)
                        }

                        DropdownMenuItem(
                            text = { Text(text = categoryText) },
                            onClick = {
                                selectedCategory = category
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val saveButtonText = stringResource(id = R.string.save)

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
                        val finalPriority = if (isImportantTask) Priority.HIGH else selectedPriority

                        if (task != null) {
                            val updatedTask = task.copy(
                                name = taskName,
                                description = taskDescription,
                                priority = finalPriority,
                                category = selectedCategory
                            )
                            viewModel.updateTask(updatedTask)
                        } else {
                            viewModel.addTask(
                                name = taskName,
                                description = taskDescription,
                                priority = finalPriority,
                                category = selectedCategory
                            )
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = taskAddedMessage,
                                duration = SnackbarDuration.Short
                            )
                        }

                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}