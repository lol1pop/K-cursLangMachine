package analyzer

import regular.Reg

//.set("abc*(a+b)^*((a+b)^*c*(a+b)^*c*(a+b)^)^*abc")
//.set("0*((0^*1+0^*1^)^+((0+1^+0)*(0*1^*0)^)^+(0+1^*0))^")

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

    private fun checkSpecialSymbol(left: Int, right: Int) {

    }

    private fun detectOperation(expression: String): Operation {
        var or = false
        var and = false
        var closeBracket = 0
        for(char in expression){
            if(isOpening(char)){
                closeBracket++
                continue
            }
            if(isClosing(char)){
                closeBracket--
                continue
            }
            if(char == '+' && closeBracket == 0) or = true
            if(char == '*' && closeBracket == 0) and = true
        }
        return when {
            !or && and -> Operation.AND
            or && !and -> Operation.OR
            else -> Operation.Mixed
        }
    }

    private fun checkBracket(expression: String): Boolean {
        for(char in expression)
            if(isBracket(char)) return true
        return false
    }

    private fun String.splitBracket(char: Char): List<String> {
        val list = mutableListOf<String>()
        var counterBracket = 0
        var next = 0
        for (i in this.indices) {
            val s = this[i]
            if (i < next) continue
            if (isOpening(this[i])) counterBracket ++
            if (isClosing(this[i])) counterBracket --
            if (counterBracket == 0 && this[i] == char) {
                list += this.substring(next, i)
                next = i + 1
            }
        }
        if(counterBracket == 0 && next < this.length) {
            list += this.substring(next, this.length)
        }
        return list
    }

    private fun listAndReg(regEx: String): List<Reg>  {
        val reg = mutableListOf<Reg>()
        val expressions = regEx.splitBracket('*')
        for(exp in expressions){
            reg += if (isOpening(exp.first()))
                if (isLoop(exp.last())) readBracket(exp.drop(1).dropLast(2), true)
                else readBracket(exp.drop(1).dropLast(1))
            else takeTerm(exp)
        }
        return reg.toList()
    }

    private fun listOrReg(regEx: String): List<Reg> {
        val reg = mutableListOf<Reg>()
        val expressions = regEx.splitBracket('+')
        for(exp in expressions){
            val detectAndExp = exp.splitBracket('*').size > 1
            reg += if(detectAndExp){
                Reg(andReg = listAndReg(exp))
            }else {
                if (isOpening(exp.first()))
                    if (isLoop(exp.last())) readBracket(exp.drop(1).dropLast(2), true)
                    else readBracket(exp.drop(1).dropLast(1))
                else takeTerm(exp)
            }
        }
        return reg.toList()
    }

    private fun readBracket(regEx: String, loop: Boolean = false): Reg {
        if(!checkBracket(regEx)) //(0+(1^+0)) |(0*(1^+0)) | (0+(1^*0)*0)
            return listTerm(regEx).copy(single = !loop) //(0+1^+0) | (0*1^*0) | (0+1^*0)
        return when(detectOperation(regEx)){
            Operation.AND -> Reg(andReg = listAndReg(regEx), single =  !loop)  //0*((0+1^+0)*(0*1^*0)^*(0+1^*0))^
            Operation.OR, Operation.Mixed -> Reg(orReg = listOrReg(regEx), single =  !loop) //0*(0+(0+1^+0)*(0*1^*0)^+(0+1^*0))^
        }
    }

    private fun listTerm(expression: String): Reg {
        return when(detectOperation(expression)) {
            Operation.OR -> listOrTerm(expression)
            Operation.AND -> listAndTerm(expression)
            Operation.Mixed -> listMixedTerm(expression)
        }
    }

    private fun listMixedTerm(substr: String): Reg {
        val list = mutableListOf<Reg>()
        val terms = substr.split("+")
        for(term in terms){
            list += listAndTerm(term)
        }
        return Reg(orReg = list.toList())
    }

    private fun listAndTerm(substr: String): Reg {
        val list = mutableListOf<Reg>()
        val terms = substr.split("*")
        for(term in terms){
            list += takeTerm(term)
        }
        return Reg(andReg = list.toList())
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

    fun stringToObject(): List<Reg> {
        return listAndReg(this.regExString)
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