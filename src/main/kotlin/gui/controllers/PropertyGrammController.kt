package gui.controllers

import gui.models.PropertyGramm
import javafx.collections.FXCollections
import tornadofx.*

class PropertyGrammController: Controller() {
    val props = FXCollections.observableArrayList<PropertyGramm>()
    init {
        props += PropertyGramm("S","0A|1A|")
        props += PropertyGramm("A", "1B|0B")
        props += PropertyGramm("B", "1S|0S")
    }

    fun addProps(prop: PropertyGramm) = props.add(prop)
    fun removeProps(prop: PropertyGramm){
        var list = props.filtered { it.value != prop.value }.toList()
        props.setAll(list)
    }

}

class ListGeneratedGrammaticController: Controller() {
    val list = FXCollections.observableArrayList<String>()
    init {
//        list += "Hello"
//        list += "It is program"
//        list += "Generated chain from Grammatical"
    }
}