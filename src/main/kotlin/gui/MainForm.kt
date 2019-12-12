package gui


import analyzer.Analyzer
import analyzer.lexical.Lexical
import gui.controllers.ComboStor
import gui.controllers.ListGeneratedGrammaticController
import gui.controllers.PropertyGrammController
import gui.models.PropertyGrammModel
import gui.models.Terminal
import gui.models.TerminalModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import regular.GeneratedFromReg
import regular.GeneratedRegExSeq
import tornadofx.*
import java.io.File
import java.time.LocalDate

class MainForm : View() {
    override val root = Form()
    val termModel = TerminalModel(Terminal())
    val comboStor: ComboStor by inject()
    val propsModel: PropertyGrammModel by inject()
    val propsStor: PropertyGrammController by inject()
    val listStore: ListGeneratedGrammaticController by inject()
    val maxField = SimpleIntegerProperty()
    var max: Int = 5
    val minField = SimpleIntegerProperty()
    var min: Int = 1
    val multiField = SimpleIntegerProperty()
    var multiSymbol = ' '
    var selectSymbol = ' '
    var result = emptyList<String>()

    init {
        with(root) {
//            menubar {
//                menu("File") {
//                    menu("Connect") {
//                        item("Facebook")
//                        item("Twitter")
//                    }
//                    separator()
//                    item("Save").action { println("Saving!") }
//                    item("Quit")
//                }
//                menu("Edit") {
//                    item("Copy")
//                    item("Paste")
//                }
//            }
            tabpane {
                tab("info") {
                    textflow {
                        text {
                            "special symbols: ( +, *, (, ), ^, #, %) " +
                                    "" +
                                    ""
                        }
                    }
                    button("   gg   ").action {
                        try {
                                throw Exception("HELOOOOELEOOSKASKBSJ")
                        } catch (ex: Exception) {
                            dialog("Exaption: ") {
                                val model = ViewModel()
                                val note = model.bind { SimpleStringProperty() }

                                field("") {
                                    textarea(note) {
                                        required()
                                        whenDocked { requestFocus() }
                                    }
                                }
                                note.value = ex.message
                            }
                        }

                    }
                }
                tab("help") {
                    textflow {
                        text { "example: \n" +
                                "(a+b+c)^*c*(a+b+c)^" +
                                "" }
                    }
                }
                tab("about") {
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
            }
            hbox {
                vbox {
                    prefWidth = 500.0
                    paddingAll = 6
                    hboxConstraints { hGrow = Priority.ALWAYS }
                    hbox {
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
                            comboStor.addItems(termModel.alphabet.value.replace(",", "").toSet().toCharArray())
                        }

                        label(" Multi Symbol ")
                        val cb = combobox<Char> {
                            items = comboStor.symbols
                            setOnAction {
                                selectedItem?.let { multiSymbol = it }
                            }
                        }
                    }
                    hbox {
                        label("Sequent:  ")
                        textfield(termModel.sequent) { required(message = "Sequent") }
                        label(" Multi   ")
                        multiField.value = min
                        val txt = textfield(multiField)
                        txt.isEditable = false
                        txt.prefWidth = 40.0
                        button("+") {
                            action {
                                min++
                                multiField.value = min
                            }
                        }
                        button("-") {
                            action {
                                if (min != 0) min--
                                multiField.value = min
                            }
                        }
                    }
                    tabpane {
                        paddingTop = 15.0
                        tab("Generated") {
                            vbox {
                                paddingVertical = 25
                                val model = ViewModel()
                                val note = model.bind { SimpleStringProperty() }
                                val txt = textarea(note) {
                                    required()
                                }
                                txt.prefHeight = 80.0
                                buttonbar {
                                    button("Generated RegEx").action {
                                        termModel.commit {
                                            note.value = GeneratedRegExSeq(termModel.alphabet.value.split(","),termModel.sequent.value,Pair(multiSymbol.toString(),multiField.value)).build()
                                        }
                                    }
                                    button("Generated Chain").action {
                                        model.commit {
                                            try {
                                                val regex = Lexical.analysis(note.value)
                                                val a = Analyzer()
                                                a.set(regex)
                                                val regGen = GeneratedFromReg(a.stringToObject())
                                                result = regGen.start(min, max).toSet().toList()
                                                listStore.list.setAll(result)
                                            } catch (ex: Exception) {
                                                dialog("Exaption: ") {
                                                    val model = ViewModel()
                                                    val note = model.bind { SimpleStringProperty() }

                                                    field("") {
                                                        textarea(note) {
                                                            required()
                                                            whenDocked { requestFocus() }
                                                        }
                                                    }
                                                    note.value = ex.message
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        tab("Open file") {
                            hbox(20) {
                                paddingTop = 15.0
                                val model = ViewModel()
                                val fileName = model.bind { SimpleStringProperty() }
                                textfield(fileName){
                                    required(message = "File name")
                                }
                                button("Generated Chain").action {
                                    model.commit {
                                        try {
                                            val data = File(fileName.value).inputStream().readBytes().toString(Charsets.UTF_8)
                                            val regex = Lexical.analysis(data)
                                            val a = Analyzer()
                                            a.set(regex)
                                            val regGen = GeneratedFromReg(a.stringToObject())
                                            result = regGen.start(min, max).toSet().toList()
                                            listStore.list.setAll(result)
                                        } catch (ex: Exception) {
                                            dialog("Exaption: ") {
                                                val model = ViewModel()
                                                val note = model.bind { SimpleStringProperty() }

                                                field("") {
                                                    textarea(note) {
                                                        required()
                                                        whenDocked { requestFocus() }
                                                    }
                                                }
                                                note.value = ex.message
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    hbox(20) {
                        label("Setup size chain  ")
                        hbox {
                            label("min:  ")
                            minField.value = min
                            val txt = textfield(minField)
                            txt.isEditable = false
                            txt.prefWidth = 30.0
                            button("+") {
                                action {
                                    min++
                                    minField.value = min
                                }
                            }
                            button("-") {
                                action {
                                    if (min != 0) min--
                                    minField.value = min
                                }
                            }
                        }
                        hbox {
                            label("max:  ")
                            maxField.value = max
                            val txt = textfield(maxField)
                            txt.isEditable = false
                            txt.prefWidth = 30.0
                            button("+") {
                                action {
                                    max++
                                    maxField.value = max
                                }
                            }
                            button("-") {
                                action {
                                    if (max != 0) max--
                                    maxField.value = max
                                }
                            }
                        }
                    }
                    scrollpane {
                        val list = listview(listStore.list) {}
                        list.prefHeight = 250.0
                        list.prefWidth = 479.0
                    }
                    button("Save file").action {
                        val fileName = "regEx_${LocalDate.now()}"
                        File(fileName).writeText(result.joinToString("\n"))
                    }
                }
            }
        }
    }

}


//dialog("Add note") {
//    val model = ViewModel()
//    val note = model.bind { SimpleStringProperty() }
//
//    field("") {
//        textarea(note) {
//            required()
//            whenDocked { requestFocus() }
//        }
//    }
//    buttonbar {
//        button("Save note").action {
//            model.commit {  }
//        }
//    }
//}