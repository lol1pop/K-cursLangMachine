import analyzer.Analyzer
import analyzer.lexical.Lexical
import regular.Reg
import regular.GeneratedFromReg
import com.mifmif.common.regex.Generex
import regular.GeneratedRegExSeq
import java.lang.Exception

import gui.MainForm
import gui.OMainForm
import javafx.application.Application
import tornadofx.*

class MainApp : App(MainForm::class)

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}


fun mainReg(){
    val regex = listOf(Reg(term = listOf("a")),
        Reg(orReg = listOf(
            Reg(andReg = listOf(Reg(term = listOf("a")))),
            Reg(andReg = listOf(Reg(term = listOf("b"),single = false),Reg(term = listOf("c"))))
        )))
    val a = Analyzer()
    a.set("s*((e+f^+g)*(e*f^*g)^+l)^")
    val regresult = a.stringToObject()
    val regGen = GeneratedFromReg(regresult)
    val result = regGen.start(1,6)
    result.forEach { println(it) }
}
fun mainAnalyzer() {
    val a = Analyzer()
    a.set("a*(a+b^*c)")
    val regresult = a.stringToObject()
    println(regresult)
}

fun main1(){
    val alphabet = "a,b,c,#,*,(,%".split(",")
    val special = listOf("*", "(", ")", "+", "^", "#", "%")
    alphabet.forEach {
        if(special.contains(it))
            throw Exception()
    }
    if (alphabet.containsAll(special))
        throw Exception()
    val reg = GeneratedRegExSeq("a,b,c".split(","),"bb",Pair("c",2)).build()
    val str = "(((a)^))  abc(( a + b   ))^  (( a + ( a + b ))^c(   a +(( a + b )+ (b)+(b^)+(((b)^)) ))^c(( a + b )^+ b )^)^abc   (( a + b )^)(((a)))"
    val k = Lexical.analysis(str)
    mainReg()
}


//genregEx(rep("abc(a+b)^((a+b)^c(a+b)^c(a+b)^)^abc",5))
// genregEx(rep("0(1+0)^((0+1)(0+1))",5))
fun genregEx(str: String) {
    val generex = Generex(str)
    generex.getMatchedStrings(100).forEach { println(it) }
//    val matchedStrs: List<String> = generex.getAllMatchedStrings()
//
//    val iterator: Iterator? = generex.iterator()
//    while (iterator?.hasNext()!!) {
//        println(iterator.next().toString() + " ")
//    }
}

fun rep(reg: String, max: Int) = reg.replace("+","|").replace("^","{0,$max}")