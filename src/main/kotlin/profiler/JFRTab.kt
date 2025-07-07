package profiler

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import profiler.listJvmProcesses

@Composable
fun JFRTab() {
    var allProcs by remember { mutableStateOf(listJvmProcesses()) }
    var filter by remember { mutableStateOf("") }
    val procs = allProcs.filter { it.second.contains(filter, ignoreCase = true) }
    var selected by remember { mutableStateOf<Pair<String, String>?>(null) }
    var duration by remember { mutableStateOf("30") }
    var status by remember { mutableStateOf("Ready") }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { allProcs = listJvmProcesses() }) {
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
            OutlinedTextField(duration, onValueChange = { duration = it }, label = { Text("Duration (s)") })
            Spacer(Modifier.width(16.dp))
            Button(enabled = selected != null, onClick = {
                status = "Recording..."
                val pid = selected!!.first
                val out = File.createTempFile("recording-", ".jfr")
                scope.launch {
                    val pb = ProcessBuilder(
                        "jcmd", pid, "JFR.start",
                        "duration=${duration}s",
                        "filename=${out.absolutePath}"
                    ).inheritIO().start()
                    status = "Will save to ${out.absolutePath}"
                    pb.waitFor()
                    duration.toIntOrNull()?.let { Thread.sleep(it*1000L) }
                    status = "Saved to ${out.absolutePath}"
//                    Desktop.getDesktop().browse(out.toURI())
                }
            }) {
                Text("Record JFR")
            }
        }

        Text("Status: $status")
    }
}