package gui.models

import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel

class Terminal {
    val alphabetProperty = SimpleStringProperty()
    val sequentProperty = SimpleStringProperty()
}

class TerminalModel(terminal: Terminal) : ViewModel() {
    val alphabet = bind {terminal.alphabetProperty}
    val sequent = bind {terminal.sequentProperty}
}