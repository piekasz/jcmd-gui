import androidx.compose.runtime.*

class PersonListState {
    var persons by mutableStateOf(listOf<Person>())
    var name by mutableStateOf("")
    var surname by mutableStateOf("")
    var selectedIndex by mutableStateOf(-1)

    fun addOrUpdate() {
        if (name.isNotBlank() && surname.isNotBlank()) {
            persons = persons.toMutableList().apply {
                if (selectedIndex >= 0) this[selectedIndex] = Person(name, surname)
                else add(Person(name, surname))
            }
            clearInput()
        }
    }

    fun edit(index: Int) {
        name = persons[index].name
        surname = persons[index].surname
        selectedIndex = index
    }

    fun delete(index: Int) {
        persons = persons.toMutableList().apply { removeAt(index) }
        if (selectedIndex == index) clearInput()
    }

    private fun clearInput() {
        name = ""
        surname = ""
        selectedIndex = -1
    }
}

