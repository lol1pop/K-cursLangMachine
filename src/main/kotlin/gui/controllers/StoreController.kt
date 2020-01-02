package gui.controllers

import javafx.collections.FXCollections
import tornadofx.*

class ComboStor: Controller() {
    var symbols = FXCollections.observableArrayList<Char>(' ')
    fun addItems(charArr: CharArray) {
        symbols.clear()
        symbols.addAll(charArr.asList())
    }

}