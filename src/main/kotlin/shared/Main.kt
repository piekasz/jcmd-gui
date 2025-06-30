package shared

import AppUI
import PersonListState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Person CRUD") {
        AppUI(PersonListState())
    }
}

