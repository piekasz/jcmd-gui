import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import profiler.AsyncProfilerTab
import profiler.JFRTab


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Async-Profiler GUI") {
        var currentTab by remember { mutableStateOf(0) }

        MaterialTheme {
            Column {
                TabRow(selectedTabIndex = currentTab) {
                    Tab(selected = currentTab == 0, onClick = { currentTab = 0 }) {
                        Text("Async Profiler")
                    }
                    Tab(selected = currentTab == 1, onClick = { currentTab = 1 }) {
                        Text("JFR Recorder")
                    }
                }
                when (currentTab) {
                    0 -> AsyncProfilerTab()
                    1 -> JFRTab()
                }
            }
        }
    }
}