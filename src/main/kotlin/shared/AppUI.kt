import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppUI(state: PersonListState) {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(state.name, onValueChange = { state.name = it }, label = { Text("Name") })
                OutlinedTextField(state.surname, onValueChange = { state.surname = it }, label = { Text("Surname") })
                Button(onClick = state::addOrUpdate) {
                    Text(if (state.selectedIndex >= 0) "Update" else "Add")
                }
            }
            Divider()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.persons.forEachIndexed { index, person ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${person.name} ${person.surname}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { state.edit(index) }) { Text("Edit") }
                            Button(onClick = { state.delete(index) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

