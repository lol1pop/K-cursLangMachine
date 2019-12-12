package gui.controllers

import gui.models.PropertyGrammItem
import javafx.collections.FXCollections
import tornadofx.*

class Store : Controller(){
    val props = FXCollections.observableArrayList<PropertyGrammItem>()

    fun addProps(value: String) = props.add(PropertyGrammItem(value))
    fun removedProps(property: PropertyGrammItem) = props.remove(property)

}

class ComboStor: Controller() {
    var symbols = FXCollections.observableArrayList<Char>(' ')
    fun addItems(charArr: CharArray) {
        symbols.clear()
        symbols.addAll(charArr.asList())
    }

}