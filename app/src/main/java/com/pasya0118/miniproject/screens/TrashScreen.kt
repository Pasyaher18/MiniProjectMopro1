package com.pasya0118.miniproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pasya0118.miniproject.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(navController: NavController, viewModel: TaskViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sampah") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        val trashTasks = viewModel.trashTasks

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            if (trashTasks.isEmpty()) {
                Text(text = "Belum ada tugas yang dihapus")
            } else {
                LazyColumn {
                    items(trashTasks) { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = task.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(onClick = {
                                        viewModel.restoreTask(task)
                                    }) {
                                        Text("Pulihkan")
                                    }
                                    TextButton(onClick = {
                                        viewModel.permanentlyDeleteTask(task)
                                    }) {
                                        Text("Hapus")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


