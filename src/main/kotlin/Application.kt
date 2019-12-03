import grammatic.GeneratedSequence
import grammatic.entity.DataGrammatics
import regular.Reg
import regular.RegExpGenerated

//import gui.MainForm
//import javafx.application.Application
//import tornadofx.*
//
//class MainApp : App(MainForm::class)
//
//fun main(args: Array<String>) {
//    Application.launch(MainApp::class.java, *args)
//}
//

data class PropertyGramm(
    val key: String,
    val value: String)

fun startGramm(nonterminal: String,
               terminal: String,
               origin: String,
               list: List<PropertyGramm>,
               min: Int,
               max: Int): List<String> {
    val data = DataGrammatics(
        nonterminalSymbols = nonterminal,
        terminalSymbols = terminal,
        originSymbol = origin,
        rulesSymbol = convertListRuleToMap(list)
    )
    val gramm = GeneratedSequence(data)
    val listChain = gramm.start(min, max)
    return listChain
}

fun convertListRuleToMap(list: List<PropertyGramm>): Map<Char,List<String>> {
    val map = list.map { it.key.toCharArray().first() to convertPropsStrToList(it.value) }.toMap()
    return map
}

fun convertPropsStrToList(props: String): List<String> {
    val str = props.replace("\\s".toRegex(), "")
    val propsList = str.split("|")
    if(propsList.last() == "|") propsList + ""
    return propsList
}

fun mainGramm() {

    val result = startGramm(
        "SABC",
        "01a",
        "S",
        listOf(
            PropertyGramm("S","0A"),
            PropertyGramm("A","0B|aB|1B"),
            PropertyGramm("B","0C|aC|1C|1"),
            PropertyGramm("C","0A|aA|1A")
        ),
        3,
        11
    )
    for(chain in result){
        println(chain)
    }
}

fun mainReg(){
    val regex = listOf(Reg(term = listOf("0")),
        Reg(reg = listOf(
            Reg(term = listOf("0","1"), single = false),
            Reg(term = listOf("0","1"))
        )))
    val regGen = RegExpGenerated(regex)
    val result = regGen.start(1,3)
    for(chain in result){
        println(chain)
    }
}

fun main(){
    mainReg()
}