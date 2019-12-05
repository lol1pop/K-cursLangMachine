package analyzer

import regular.Reg

class Analyzer {

    private var regExString = ""
    private var regExReg = Reg()

    fun set(regEx: String) { this.regExString = regEx }
    fun getString() = this.regExString
    fun set(regEx: Reg) { this.regExReg = regEx }
    fun getReg() = this.regExReg

    private val SPECIALSYMBOL = listOf('+', '*', '^')

    private fun findClosingBracket(index: Int) {
        for(i in index until regExString.length) {

        }
    }

    private fun checkSpecialSymbol(left: Int, right: Int) {

    }

    private fun checkBracket(left: Int, right: Int) {

    }

    private fun readString(left: Int, right: Int) {

    }

    private fun readString() {

    }

    fun stringToObject():Reg {

        return Reg()
    }

    fun regToString(): String {
        return ""
    }
}

fun foo(vararg c: String) {
    outer@ for (n in 2..100) {
        for (d in 2 until n) {
            if (n % d == 0) continue@outer
        }
        println("$n is prime")
    }
}