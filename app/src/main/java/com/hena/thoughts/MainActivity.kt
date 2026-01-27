package com.hena.thoughts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var philosophyManager: PhilosophyManager
    private lateinit var settingsManager: SettingsManager
    private lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        philosophyManager = PhilosophyManager(this)
        settingsManager = SettingsManager(this)
        alarmScheduler = AlarmScheduler(this)

        requestNotificationPermission()
        alarmScheduler.schedule()

        setContent {
            MaterialTheme {
                var screen by remember { mutableStateOf<Screen>(Screen.Main) }
                var philosophies by remember { mutableStateOf(philosophyManager.loadAll()) }

                when (val current = screen) {
                    is Screen.Main -> MainScreen(
                        philosophies = philosophies,
                        onAdd = { screen = Screen.Edit(null) },
                        onEdit = { screen = Screen.Edit(it) },
                        onDelete = { id -> philosophies = philosophyManager.delete(id) },
                        onSettings = { screen = Screen.Settings }
                    )
                    is Screen.Edit -> EditScreen(
                        philosophy = current.philosophy,
                        onSave = { p ->
                            philosophies = if (p.id == 0) {
                                philosophyManager.add(p)
                            } else {
                                philosophyManager.update(p)
                            }
                            screen = Screen.Main
                        },
                        onBack = { screen = Screen.Main }
                    )
                    is Screen.Settings -> SettingsScreen(
                        settingsManager = settingsManager,
                        alarmScheduler = alarmScheduler,
                        onBack = { screen = Screen.Main }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                    .launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

sealed class Screen {
    data object Main : Screen()
    data class Edit(val philosophy: Philosophy?) : Screen()
    data object Settings : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    philosophies: List<Philosophy>,
    onAdd: () -> Unit,
    onEdit: (Philosophy) -> Unit,
    onDelete: (Int) -> Unit,
    onSettings: () -> Unit
) {
    val grouped = philosophies.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HenaThoughts") },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            grouped.forEach { (category, items) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(items, key = { it.id }) { philosophy ->
                    PhilosophyCard(
                        philosophy = philosophy,
                        onEdit = { onEdit(philosophy) },
                        onDelete = { onDelete(philosophy.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhilosophyCard(
    philosophy: Philosophy,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = philosophy.title, style = MaterialTheme.typography.titleSmall)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = philosophy.content, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    philosophy: Philosophy?,
    onSave: (Philosophy) -> Unit,
    onBack: () -> Unit
) {
    var category by remember { mutableStateOf(philosophy?.category ?: "") }
    var title by remember { mutableStateOf(philosophy?.title ?: "") }
    var content by remember { mutableStateOf(philosophy?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (philosophy == null) "Add" else "Edit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(
                            Philosophy(
                                id = philosophy?.id ?: 0,
                                category = category.ifBlank { "Uncategorized" },
                                title = title,
                                content = content
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    alarmScheduler: AlarmScheduler,
    onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(settingsManager.isNotificationEnabled) }
    var interval by remember { mutableStateOf(settingsManager.intervalHours.toString()) }
    var startHour by remember { mutableStateOf(settingsManager.activeStartHour.toString()) }
    var endHour by remember { mutableStateOf(settingsManager.activeEndHour.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifications")
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        settingsManager.isNotificationEnabled = it
                        if (it) alarmScheduler.schedule() else alarmScheduler.cancel()
                    }
                )
            }

            OutlinedTextField(
                value = interval,
                onValueChange = {
                    interval = it
                    it.toIntOrNull()?.let { h ->
                        if (h in 1..24) {
                            settingsManager.intervalHours = h
                            alarmScheduler.schedule()
                        }
                    }
                },
                label = { Text("Interval (hours, 1-24)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startHour,
                onValueChange = {
                    startHour = it
                    it.toIntOrNull()?.let { h ->
                        if (h in 0..23) {
                            settingsManager.activeStartHour = h
                            alarmScheduler.schedule()
                        }
                    }
                },
                label = { Text("Active start hour (0-23)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endHour,
                onValueChange = {
                    endHour = it
                    it.toIntOrNull()?.let { h ->
                        if (h in 0..23) {
                            settingsManager.activeEndHour = h
                            alarmScheduler.schedule()
                        }
                    }
                },
                label = { Text("Active end hour (0-23)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
