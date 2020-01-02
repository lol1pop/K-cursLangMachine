package gui.controllers

import javafx.collections.FXCollections
import tornadofx.*

class ListGeneratedGrammaticController: Controller() {
    val list = FXCollections.observableArrayList<String>()
    init {
//        list += "Hello"
    }
}