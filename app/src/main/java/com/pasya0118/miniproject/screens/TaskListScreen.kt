package com.pasya0118.miniproject.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.pasya0118.miniproject.BuildConfig
import com.pasya0118.miniproject.R
import com.pasya0118.miniproject.data.User
import com.pasya0118.miniproject.model.Category
import com.pasya0118.miniproject.model.Priority
import com.pasya0118.miniproject.model.Task
import com.pasya0118.miniproject.navigation.Screen
import com.pasya0118.miniproject.util.SettingsDataStore
import com.pasya0118.miniproject.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GridTaskItem(
    task: Task,
    onClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onToggleComplete: (String) -> Unit,
    onShare: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick(task) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDeleteClick(task) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Tugas", tint = MaterialTheme.colorScheme.error)
                }


                IconButton(onClick = { onToggleComplete(task.id) }) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.Check else Icons.Default.Refresh,
                        contentDescription = if (task.isCompleted) "Tandai Belum Selesai" else "Tandai Selesai",
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { onShare(task) }) {
                    Icon(Icons.Default.Share, contentDescription = "Bagikan Tugas", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    onAddTask: () -> Unit,
    onTaskClick: (Task) -> Unit
) {
    val context = LocalContext.current

    val taskListTitle = stringResource(id = R.string.task_list)
    val addTaskDesc = stringResource(id = R.string.add_task)
    val emptyListText = stringResource(id = R.string.empty_list)
    val shareTaskText = stringResource(id = R.string.share_task)

    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    val dataStore = SettingsDataStore(LocalContext.current)
    val user by dataStore.userFlow.collectAsState(User())
    val showList by dataStore.layoutFlow.collectAsState(true)

    LaunchedEffect(Unit) {
        viewModel.fetchTasksFromApi()
        Log.d("TaskListScreen", "fetchTasksFromApi() dipanggil")
    }

    if (showDialog && taskToDelete != null) {
        DisplayAlertDialog(
            onDismissRequest = {
                showDialog = false
                taskToDelete = null
            },
            onConfirmation = {
                viewModel.deleteTask(taskToDelete!!.id)
                showDialog = false
                taskToDelete = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(taskListTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!showList)
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                if (showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_view_list_24
                            ),
                            contentDescription = stringResource(
                                if (showList) R.string.grid
                                else R.string.list
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.Trash.route)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = stringResource(R.string.trash),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        }
                        else {
                            Log.d("SIGN-IN", "User: $user")
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = addTaskDesc
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (viewModel.tasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_list),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = emptyListText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                if (showList) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(viewModel.tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task.id) },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, task.name)
                                    putExtra(Intent.EXTRA_TEXT, "${task.name}: ${task.description}")
                                }
                                context.startActivity(Intent.createChooser(
                                    shareIntent,
                                    shareTaskText
                                ))
                            },
                            onClick = { onTaskClick(task) },
                            onDeleteClick = {
                                showDialog = true
                                taskToDelete = task
                            }

                        )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(viewModel.tasks) { task ->
                            GridTaskItem(

                                task = task,
                                onToggleComplete = { viewModel.toggleTaskCompletion(task.id) },
                                onShare = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, task.name)
                                        putExtra(Intent.EXTRA_TEXT, "${task.name}: ${task.description}")
                                    }
                                    context.startActivity(Intent.createChooser(
                                        shareIntent,
                                        shareTaskText
                                    ))
                                },
                                onClick = { onTaskClick(task) },
                                onDeleteClick = {
                                    showDialog = true
                                    taskToDelete = task
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val shareText = stringResource(id = R.string.share)
    val deleteText = stringResource(id = R.string.delete)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleComplete() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = shareText
                        )
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = deleteText,
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriorityBadge(priority = task.priority)
                CategoryBadge(category = task.category)
            }
        }
    }
}

suspend fun signIn(context: Context, dataStore: SettingsDataStore) {
    val googleIdOption: GetGoogleIdOption= GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error:${e.errorMessage}")
    }
}

private fun handleSignIn(
    result: androidx.credentials.GetCredentialResponse,
    dataStore: SettingsDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500) // Orange
        Priority.LOW -> Color.Green
    }

    val text = when (priority) {
        Priority.HIGH -> stringResource(id = R.string.priority_high)
        Priority.MEDIUM -> stringResource(id = R.string.priority_medium)
        Priority.LOW -> stringResource(id = R.string.priority_low)
    }

    Badge(text = text, color = color)
}

@Composable
fun CategoryBadge(category: Category) {
    val text = when (category) {
        Category.WORK -> stringResource(id = R.string.category_work)
        Category.PERSONAL -> stringResource(id = R.string.category_personal)
        Category.SHOPPING -> stringResource(id = R.string.category_shopping)
        Category.OTHER -> stringResource(id = R.string.category_other)
    }

    Badge(text = text, color = MaterialTheme.colorScheme.tertiary)
}

@Composable
fun Badge(text: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}