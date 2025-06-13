package com.pasya0118.miniproject.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pasya0118.miniproject.R
import com.pasya0118.miniproject.data.User
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val shareTaskString = stringResource(id = R.string.share_task)
    val backString = stringResource(id = R.string.back)
    val shareString = stringResource(id = R.string.share)
    val deleteString = stringResource(id = R.string.delete)
    val taskNameString = stringResource(id = R.string.task_name)
    val taskDescriptionString = stringResource(id = R.string.task_description)
    val priorityString = stringResource(id = R.string.priority)
    val categoryString = stringResource(id = R.string.category)

    if (showDialog) {
        DisplayAlertDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                viewModel.deleteTask(task.id)
                onNavigateBack()
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task.name) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = backString
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("edit_task")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, task.name)
                                putExtra(Intent.EXTRA_TEXT, "${task.name}: ${task.description}")
                            }
                            context.startActivity(Intent.createChooser(
                                shareIntent,
                                shareTaskString
                            ))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = shareString
                        )
                    }
                    IconButton(
                        onClick = { showDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = deleteString,
                            tint = Color.Red
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val statusBackgroundColor = if (task.isCompleted) {
                Color(0xFF4CAF50)
            } else {
                Color(0xFFFFA000)
            }

            val statusText = if (task.isCompleted) {
                "Selesai"
            } else {
                "Belum Selesai"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusBackgroundColor.copy(alpha = 0.2f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        color = statusBackgroundColor
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = taskNameString,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = taskDescriptionString,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = task.description.ifEmpty { "-" },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = priorityString,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            val priorityText = when (task.priority) {
                                com.pasya0118.miniproject.model.Priority.HIGH ->
                                    stringResource(id = R.string.priority_high)
                                com.pasya0118.miniproject.model.Priority.MEDIUM ->
                                    stringResource(id = R.string.priority_medium)
                                com.pasya0118.miniproject.model.Priority.LOW ->
                                    stringResource(id = R.string.priority_low)
                            }

                            Text(
                                text = priorityText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Column {
                            Text(
                                text = categoryString,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            val categoryText = when (task.category) {
                                com.pasya0118.miniproject.model.Category.WORK ->
                                    stringResource(id = R.string.category_work)
                                com.pasya0118.miniproject.model.Category.PERSONAL ->
                                    stringResource(id = R.string.category_personal)
                                com.pasya0118.miniproject.model.Category.SHOPPING ->
                                    stringResource(id = R.string.category_shopping)
                                com.pasya0118.miniproject.model.Category.OTHER ->
                                    stringResource(id = R.string.category_other)
                            }

                            Text(
                                text = categoryText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
