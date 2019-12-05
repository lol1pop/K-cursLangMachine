package analyzer

import regular.Reg

class Analyzer {

    private var regExString = ""
    private var regExReg = Reg()

    fun set(regEx: String) { this.regExString = regEx }
    fun getString() = this.regExString
    fun set(regEx: Reg) { this.regExReg = regEx }
    fun getReg() = this.regExReg

    private fun isSpecial(char: Char) = listOf('+', '*', '^', '(', ')').contains(char)
    private fun isBracket(char: Char) = listOf('(',')').contains(char)
    private fun isLoop(char: Char) = ('^' == char)
    private fun isOpening(char: Char) = ('(' == char)
    private fun isClosing(char: Char) = (')' == char)

    private fun findClosingBracket(index: Int): Int {
        for(i in index until regExString.length) {

        }
        return 0
    }

    private fun checkSpecialSymbol(left: Int, right: Int) {

    }

    private fun checkBracket(left: Int, right: Int) {

    }

    private fun readString(left: Int, right: Int): Reg {

        return Reg()
    }

    private fun readString(): List<Reg> {
        val reg = mutableListOf<Reg>()
        var next = 0
        for(i in regExString.indices){
            if(i < next) continue
            val char = regExString[i]
            if(isOpening(char)){
                next = findClosingBracket(i)
                reg += readString(i, next)
            } else {
                if(!isSpecial(char)){
                    next = endTerm(i)
                    reg += takeTerm(i, next)
                } else {
                    if (char == '*') continue
                    //TODO: exception
                }
            }
        }
        return reg.toList()
    }

    private fun takeTerm(i: Int, next: Int): Reg {
        val term = regExString.substring(i, next)
        return  if (isLoop(regExString[next])) Reg(term = listOf(term), single = false)
        else Reg(term = listOf(term))
    }

    private fun endTerm(index: Int): Int {
        for (i in index until regExString.length)
            if (isSpecial(regExString[i]))
                return i
        return index
    }

    fun stringToObject(): List<Reg> {

        return readString()
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