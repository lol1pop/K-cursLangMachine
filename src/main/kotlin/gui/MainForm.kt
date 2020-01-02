package gui


import analyzer.Analyzer
import analyzer.lexical.Lexical
import com.GenRegExp
import gui.controllers.ComboStor
import gui.controllers.ListGeneratedGrammaticController
import gui.models.Terminal
import gui.models.TerminalModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import regular.GeneratedFromReg
import tornadofx.*
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MainForm : View() {
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
    var reg = ""
    init {
        with(root) {
            menubar {
                menu("View") {
                    item("About").action { textFlowAbout() }
                }
//                menu("tools") {
//                    menu("RegEx") {
//                        item("gen").action { dopgen() }
//                    }
//                }
            }
            tabpane {
                tab("info") {
                    this += textFlowInfo()
                }
                tab("help") {
                    this += textFlowGuid()
                }
            }
            hbox {
                vbox {
                    prefWidth = 500.0
                    paddingAll = 0
                    hboxConstraints { hGrow = Priority.ALWAYS }
                    hbox {
                        paddingVertical = 15
                        label("Alphabet: ")
                        textfield(termModel.alphabet) {
                            required(message = "Alphabet")
                            whenDocked { requestFocus() }
                            setOnAction {
                                comboStor.symbols =
                                    FXCollections.observableArrayList<Char>(termModel.alphabet.value.first())
                            }
                        }
                        setOnKeyReleased {
                            if(checkSpecialSymbol(termModel.alphabet.value)) termModel.alphabet.value = finAndDeleteSpecialSymbol(termModel.alphabet.value)
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
                        textfield(termModel.sequent) {
                            //required(message = "Sequent")
                        }
                        setOnKeyReleased {
                            if(checkSpecialSymbol(termModel.sequent.value)) termModel.sequent.value = finAndDeleteSpecialSymbol(termModel.sequent.value)
                        }
                        label(" Multi   ")
                        this += boxRangeMulti()
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
                                        val s =
                                        termModel.commit {
//                                            note.value = GeneratedRegExSeq(
//                                                termModel.alphabet.value.split(","),
//                                                if(termModel.sequent.value.isNullOrBlank()) "" else termModel.sequent.value,
//                                                Pair(multiSymbol.toString(), multiField.value)
//                                            ).build()
                                            note.value = GenRegExp(
                                                if(termModel.sequent.value.isNullOrBlank()) "" else termModel.sequent.value,
                                                termModel.alphabet.value.split(",").toTypedArray(),
                                                if(multiSymbol.toString().isNullOrBlank()) termModel.alphabet.value.split(",").first() else multiSymbol.toString(),
                                                multiField.value
                                            ).build()
                                            reg = note.value
                                        }
                                    }
                                    button("Generated Chain").action {
                                        //model.commit {}
                                        if(!note.value.isNullOrBlank()){
                                            generatedChainFromRegEx(note.value)
                                        } else {
                                            listStore.list.setAll(listOf("Ты победитель вот тебе пустая цепочка "))
                                        }
                                    }
                                }
                            }
                        }
                        tab("Open file") {
                            this += generatedFromFile()
                        }
                    }
                    hbox(20) {
                        label("Setup size chain  ")
                        this += boxRangeMin()
                        this += boxRangeMax()
                    }
                    scrollpane {
                        val list = listview(listStore.list) {}
                        list.prefHeight = 250.0
                        list.prefWidth = 479.0
                    }
                    buttonbar {
                        button("Save file").action {
                            val data = "alphabet: ${termModel.alphabet.value}\n" +
                                    "sequence: ${termModel.sequent.value}\n" +
                                    "multiSymbol: $multiSymbol\n" +
                                    "multi: ${multiField.value}\n" +
                                    "min: $min max: $max\n" +
                                    "reg: $reg"
                            val chain = result.joinToString("\n")
                            File("regEx_${LocalDate.now()}_${LocalTime.now().second}").writeText(
                                data + chain
                        ) }
                        button("Clean Result").action { listStore.list.clear() }
                    }
                }
            }
        }
    }


    private fun generatedFromFile() = hbox(20) {
        paddingTop = 15.0
        val model = ViewModel()
        val fileName = model.bind { SimpleStringProperty() }
        textfield(fileName) { required(message = "File name") }
        button("Sequence").action {
            model.commit {
                val data = File(fileName.value).inputStream().readBytes().toString(Charsets.UTF_8)
                generatedChainFromRegEx(data)
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

    private fun generatedChainFromRegEx(regEx: String) = try {
        val regex = Lexical.analysis(regEx)
        val a = Analyzer()
        a.set(regex)
        val r = a.stringToObject()
        println(r)
        val regGen = GeneratedFromReg(r)
        checkAndSwapRange()
        result = regGen.start(min, max).toSet().toList()
        listStore.list.setAll(result)
    } catch (ex: Exception) {
        exceptionDialog(ex.message.toString())
    }

    private fun checkAndSwapRange() {
        if (min > max) {
            val temp = min
            min = max
            max = temp
        }
    }

    private fun isSpecial(char: Char) = listOf('+', '*', '^', '(', ')', '%', '#','&').contains(char)
    private fun checkSpecialSymbol(data: String): Boolean {
        for (char in data)
            if (isSpecial(char)) return true
        return false
    }
    private fun finAndDeleteSpecialSymbol(data: String): String {
        return data.replace("""([()+*^%#]){1,}""".toRegex(),"")
    }


    private fun boxRangeMin() = hbox {
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

    private fun boxRangeMax() = hbox {
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

    private fun boxRangeMulti() = hbox {
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


    private fun textFlowInfo() = textflow {
        text("special symbols: ( +, *, (, ), ^, #, %) \n" +
                "dont be used when writing an alphabet, sequence \n") {
            font = Font(14.0)
        }
        text("operation[+] must be wrapped in brackets \n" +
                "example: (a+b) ") {
            fill = Color.RED
            font = Font(18.0)
        }
    }

    private fun textFlowGuid() = textflow {
        text(
            "example: \n" +
                    "(a^+b+c)*c*(a+b^*c)^" +
                    "\n" +
                    "operator[+] - or \n" +
                    "operator[-] - and \n" +
                    "operator[^] - loop (null or more) \n" +
                    "operators[(,)] - set the order \n"
        ) {
            font = Font(12.0)
        }
    }

    private fun textFlowAbout() = dialog("About:") {
        textflow {
            text(
                "Тема работы: \n" +
                        "Написать программу, которая по предложенному описанию языка \n" +
                        "построит регулярное выражение, задающее этот язык, \n" +
                        "и сгенерирует с его помощью все цепочки языка в заданном диапазоне длин. \n" +
                        "Предусмотреть также возможность \n" +
                        "генерации цепочек по введённому пользователем РВ (в рамках варианта).\n" +
                        "Варианты задания языка: \n" +
                        " Алфавит, кратность вхождения некоторого символа алфавита \n" +
                        "во все цепочки языка и заданная подцепочка всех цепочек языка. \n"
            ) {
                font = Font(18.0)
            }
            text("Кровяков Денис, Вариант 9  ") {
                fill = Color.PURPLE
                font = Font(16.0)
            }
            text("ИП-612") {
                fill = Color.ORANGE
                font = Font(16.0)
            }
        }
    }


    private fun dopgen() = dialog("RegEx") {
        val model = ViewModel()
        val note = model.bind { SimpleStringProperty() }
        val txt = textarea(note) { required() }
        txt.prefHeight = 80.0
        button("Generated RegEx").action {
            termModel.commit {
                note.value = GenRegExp(
                    if(termModel.sequent.value.isNullOrBlank()) "" else termModel.sequent.value,
                    termModel.alphabet.value.split(",").toTypedArray(),
                    multiSymbol.toString(),
                    multiField.value
                ).build()
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