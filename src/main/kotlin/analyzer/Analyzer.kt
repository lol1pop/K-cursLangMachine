package analyzer

import javafx.geometry.Orientation
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
        var counterBracket = 0
        for(i in index until regExString.length) {
            val char = regExString[i]
            if (counterBracket == 0 && isClosing(char)) return i
            if(isOpening(char)) counterBracket ++
            if (isClosing(char)) counterBracket --
        }
        return index
    }

    private fun checkSpecialSymbol(left: Int, right: Int) {

    }

    private fun detectOperation(left: Int, right: Int): Operation {
        var or = false
        var and = false
        var next = 0
        for(i in left until right){
            if(i < next)
                continue
            val char = regExString[i]
            if(isOpening(char)) {
                next = findClosingBracket(i + 1) + 1
                continue
            }
            if(char == '+') or = true
            if(char == '*') and = true
        }
        return when {
            !or && and -> Operation.AND
            or && !and -> Operation.OR
            else -> Operation.Mixed
        }
    }

    private fun checkBracket(left: Int, right: Int): Boolean {
        for(i in left until right)
            if(isBracket(regExString[i])) return true
        return false
    }

    private fun readString(left: Int, right: Int): Reg {
        var reg = Reg()
        var next = 0
        for (i in left until right) {
            if(i < next) continue
            val char = regExString[i]
            if(isOpening(char)) {

            } else {
                if(!isSpecial(char)) {
                    if(!checkBracket(left, right)) {
                        return listTerm(left, right)
                    } else {
                        when(detectOperation(left, right)) {
                            Operation.AND -> true
                            Operation.OR -> true
                            Operation.Mixed -> true
                        }
                    }
                } else {
                    //TODO: exception
                }
            }
        }
        return reg
    }

    private fun readString(): List<Reg> {
        val reg = mutableListOf<Reg>()
        var next = 0
        for(i in regExString.indices){
            if(i < next)
                continue
            val char = regExString[i]
            if(isOpening(char)){
                next = findClosingBracket(i + 1)
                reg += readString(i + 1, next - 1)
                next++
                continue
            } else {
                if(!isSpecial(char)){
                    next = endTerm(i)
                    reg += takeTerm(i, next)
                } else {
                    if (char == '*' || char == '^')
                        continue
                    //TODO: exception
                }
            }
        }
        return reg.toList()
    }

    private fun listTerm(left: Int, right: Int): Reg {
        val substr = regExString.substring(left, right + 1)
        return when(detectOperation(left, right)) {
            Operation.OR -> listOrTerm(substr)
            Operation.AND -> listAndTerm(substr)
            Operation.Mixed -> listMixedTerm(substr)
        }
    }

    private fun listMixedTerm(substr: String): Reg {
        val list = mutableListOf<Reg>()
        val terms = substr.split("+")
        for(term in terms){
            list += listAndTerm(term)
        }
        return Reg(termReg = list.toList())
    }

    private fun listAndTerm(substr: String): Reg {
        val list = mutableListOf<Reg>()
        val terms = substr.split("*")
        for(term in terms){
            list += takeTerm(term)
        }
        return Reg(reg = list.toList())
    }

    private fun listOrTerm(substr: String): Reg {
        val list = mutableListOf<String>()
        val terms = substr.split("+")
        for(term in terms){
                list += takeTerm(term).term.first()
        }
        return Reg(term = list.toList())
    }

    private fun takeTerm(term: String): Reg {
        return if(isLoop(term.last())) Reg(term = listOf(term.dropLast(1)), single = false)
        else Reg(term = listOf(term))
    }

    private fun takeTerm(left: Int, right: Int): Reg {
        val term = regExString.substring(left, right + 1)
            .removeSuffix("*").removeSuffix("+")
        return  takeTerm(term)
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

    enum class Operation {
        OR,
        AND,
        Mixed
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