package gui

import analyzer.Analyzer
import analyzer.lexical.Lexical
import gui.controllers.ComboStor
import gui.controllers.ListGeneratedGrammaticController
import gui.models.Terminal
import gui.models.TerminalModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import regular.GeneratedFromReg
import regular.GeneratedRegExSeq
import tornadofx.*
import java.io.File
import java.time.LocalDate

class OMainForm : View() {
    override val root = Form()
    val termModel = TerminalModel(Terminal())
    val comboStor: ComboStor by inject()
    val listStore: ListGeneratedGrammaticController by inject()
    val maxField = SimpleIntegerProperty()
    var max: Int = 5
    val minField = SimpleIntegerProperty()
    var min: Int = 1
    val multiField = SimpleIntegerProperty()
    var multiSymbol = ' '
    var result = emptyList<String>()

    init {
        with(root) {
            menubar {
                menu("File") {
                    menu("Open RegEx") {
                        item("Txt File").action { generatedFromFile() }
                    }
                    separator()
                    item("Save").action { File("regEx_${LocalDate.now()}").writeText(result.joinToString("\n")) }
                }
                menu("View") {
                    item("Info").action { textFlowInfo() }
                    item("Guid").action { textFlowGuid() }
                    item("About").action { textFlowAbout() }
                }
            }
            hbox(10) {
                paddingVertical = 15
                label("Alphabet: ")
                textfield(termModel.alphabet) {
                    required(message = "Alphabet")
                    whenDocked { requestFocus() }
                    setOnAction {
                        comboStor.symbols =  FXCollections.observableArrayList<Char>(termModel.alphabet.value.first())
                    }
                }
                setOnKeyReleased {
                    comboStor.addItems(termModel.alphabet.value.replace(" ", "").toSet().toCharArray())
                }
                label("Sequent:  ")
                textfield(termModel.sequent) { required(message = "Sequent") }

            }
            hbox(10) {
                label("Multi Symbol")
                val cb = combobox<Char> {
                    items = comboStor.symbols
                    setOnAction {
                        selectedItem?.let { multiSymbol = it }
                    }
                }
                cb.prefWidth = 130.0
                hbox{
                label("Multi:            ")
                multiField.value = min
                val txt = textfield(multiField)
                txt.isEditable = false
                txt.prefWidth = 40.0
                button("plus") {
                    action {
                        min++
                        multiField.value = min
                    }
                    prefWidth = 50.0
                }
                button("minus") {
                    action {
                        if (min != 0) min--
                        multiField.value = min
                    }
                    prefWidth = 50.0
                }}
            }
            vbox {
                paddingVertical = 25
                val model = ViewModel()
                val note = model.bind { SimpleStringProperty() }
                val txt = textarea(note) { required() }
                txt.prefHeight = 80.0
                button("Generated RegEx").action {
                    termModel.commit {
                        note.value = GeneratedRegExSeq(
                            termModel.alphabet.value.split(","),
                            termModel.sequent.value,
                            Pair(multiSymbol.toString(), multiField.value)
                        ).build()
                    }
                }
                vbox(10){
                    label("Range for Sequence")
                    this += boxRangeMin()
                    this += boxRangeMax()
                }
                button("Sequence").action {
                    model.commit {
                        generatedChainFromRegEx(note.value)
                    }
                }
            }
            scrollpane {
                val list = listview(listStore.list) {}
                list.prefHeight = 250.0
                list.prefWidth = 479.0
            }
        }
    }


    private fun generatedFromFile() = dialog("Generated from file"){
        hbox(20) {
            paddingTop = 15.0
            val model = ViewModel()
            val fileName = model.bind { SimpleStringProperty() }
            textfield(fileName){ required(message = "File name") }
            button("Sequence").action {
                model.commit {
                    val data = File(fileName.value).inputStream().readBytes().toString(Charsets.UTF_8)
                    generatedChainFromRegEx(data)
                }
            }
        }
        vbox(10){
            label("Range for Sequence")
            this += boxRangeMin()
            this += boxRangeMax()
        }
    }

    private fun textFlowInfo() = dialog("Info: ") {
        textflow {
            text( "special symbols: ( +, *, (, ), ^, #, %) ") {}
        }
    }
    private fun textFlowGuid() = dialog("Guid: ") {
        textflow {
            text( "example: \n" +
                    "(a+b+c)^*c*(a+b+c)^") {}
        }
    }
    private fun textFlowAbout() = dialog("About: ") {
        textflow {
            text("Кровяков Денис  ") {
                fill = Color.PURPLE
                font = Font(20.0)
            }
            text("ИП-612") {
                fill = Color.ORANGE
                font = Font(28.0)
            }
        }
    }

    private fun exceptionDialog(message: String) = dialog("Exaption: ") {
        val model = ViewModel()
        val note = model.bind { SimpleStringProperty() }

        field("") {
            textarea(note) {
                required()
                whenDocked { requestFocus() }
            }
        }
        note.value = message
    }
    private fun generatedChainFromRegEx(regEx: String) =  try {
        val regex = Lexical.analysis(regEx)
        val a = Analyzer()
        a.set(regex)
        val regGen = GeneratedFromReg(a.stringToObject())
        checkAndSwapRange()
        result = regGen.start(min, max).toSet().toList()
        listStore.list.setAll(result)
    } catch (ex: Exception) {
        exceptionDialog(ex.message.toString())
    }

    private fun checkAndSwapRange() {
        if(min > max) {
            val temp = min
            min = max
            max = temp
        }
    }
    private fun boxRangeMin() =  hbox {
            label("min size Sequence:  ")
            minField.value = min
            val txt = textfield(minField)
            txt.isEditable = false
            txt.prefWidth = 30.0
            button("plus") {
                action {
                    min++
                    minField.value = min
                }
                prefWidth = 50.0
            }
            button("minus") {
                action {
                    if (min != 0) min--
                    minField.value = min
                }
                prefWidth = 50.0
            }
        }
    private fun boxRangeMax() =  hbox {
            label("max size Sequence:  ")
            maxField.value = max
            val txt = textfield(maxField)
            txt.isEditable = false
            txt.prefWidth = 30.0
            button("plus") {
                action {
                    max++
                    maxField.value = max
                }
                prefWidth = 50.0
            }
            button("minus") {
                action {
                    if (max != 0) max--
                    maxField.value = max
                }
                prefWidth = 50.0
            }
        }

}