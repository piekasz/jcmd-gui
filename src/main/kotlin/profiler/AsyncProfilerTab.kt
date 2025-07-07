package profiler

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.awt.Desktop
import profiler.listJvmProcesses
import profiler.runAsyncProfiler
import profiler.openHtmlInBrowser

@Composable
fun AsyncProfilerTab() {
    val profPath = remember { "/Users/piotrpiekarski/Tools/async-profiler-4.0-macos/bin/asprof" }
    var allProcs by remember { mutableStateOf(listJvmProcesses()) }
    var filter by remember { mutableStateOf("") }
    val procs = allProcs.filter { it.second.contains(filter, ignoreCase = true) }
    var selected by remember { mutableStateOf<Pair<String, String>?>(null) }
    var event by remember { mutableStateOf("cpu") }
    var duration by remember { mutableStateOf("30") }
    var status by remember { mutableStateOf("Ready") }
    var elapsed by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { allProcs = listJvmProcesses(); status = "Refreshed" }) {
                Text("Refresh JVM List")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                filter,
                onValueChange = { filter = it },
                label = { Text("Search") },
                modifier = Modifier.weight(1f)
            )
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            procs.forEach { (pid, name) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected?.first == pid, onClick = { selected = pid to name })
                    Text("$pid — $name")
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Event:")
            Spacer(Modifier.width(8.dp))
            DropdownSelector(listOf("cpu", "alloc", "lock", "wall", "nativemem", "cache-misses"), event) { event = it }
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(duration, onValueChange = { duration = it }, label = { Text("Duration (s)") })
        }

        Button(enabled = selected != null, onClick = {
            status = "Profiling..."
            elapsed = 0
            val pid = selected!!.first
            val out = File.createTempFile("flame-", ".html")
            scope.launch {
                val total = duration.toIntOrNull() ?: 30
                val timerJob = launch {
                    while (elapsed < total) {
                        delay(1000)
                        elapsed++
                    }
                }
                val ok = runAsyncProfiler(profPath, pid, event, total, out)
                timerJob.cancel()
                status = if (ok) "Done: ${out.absolutePath}" else "Failed"
                if (ok) openHtmlInBrowser(out)
            }
        }) {
            Text("Run Profiler")
        }

        Text("Status: $status")
        if (status == "Profiling...") Text("Seconds elapsed: $elapsed")
    }
}

@Composable
fun DropdownSelector(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { Text(selected) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(onClick = {
                    onSelected(it)
                    expanded = false
                }) {
                    Text(it)
                }
            }
        }
    }
}