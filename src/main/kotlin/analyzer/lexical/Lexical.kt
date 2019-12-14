package analyzer.lexical

import kotlin.Exception

object Lexical {
    private fun isOperation(char: Char) = listOf('+', '*').contains(char)
    private fun isSpecial(char: Char) = listOf('+', '*', '^', '(', ')').contains(char)
    private fun isBracket(char: Char) = listOf('(',')').contains(char)
    private fun isLoop(char: Char) = ('^' == char)
    private fun isOpening(char: Char) = ('(' == char)
    private fun isClosing(char: Char) = (')' == char)

    private fun withoutSpaces(regExp: String): String = regExp.replace(Regex("\\s"),"")

    private fun findBracket(expression: String): Boolean {
        for(char in expression)
            if(isBracket(char)) return true
        return false
    }

    private fun findOperations(expression: String): Boolean {
        for(char in expression)
            if(isOperation(char)) return true
        return false
    }

    private fun findLoop(expression: String): Boolean {
        for(char in expression)
            if(isLoop(char)) return true
        return false
    }

    private fun String.separatedBracket(): List<String> {
        val list = mutableListOf<String>()
        var counterBracket = 0
        var openBracket = 0
        var closeBracket = 0
        var next = 0
        for (i in this.indices) {
            if (isOpening(this[i]) && counterBracket == 0) openBracket = i
            if (isOpening(this[i])) counterBracket ++
            if (isClosing(this[i]) && counterBracket == 1) closeBracket = i
            if (isClosing(this[i])) counterBracket --
            if (counterBracket == 0 && closeBracket != 0) {
                list += this.substring(openBracket + 1, closeBracket)
                openBracket = 0
                closeBracket = 0
            }
        }
        return list
    }

    private fun findSeparatedBracket(exps: List<String>): List<String> {
        val list = mutableListOf<String>()
        for(exp in exps ){
            if(findBracket(exp) && !findOperations(exp)) {
                list += "(${exp})"
                continue
            } else if(!findOperations(exp)) {
                list += "(${exp})"
                continue
            }
            if(findBracket(exp)){
                list += findSeparatedBracket(exp.separatedBracket())
            }
        }
        return list
    }

    private fun removeDoubleBracket(regExp: String): String {
        val removeDoubleBracket = findSeparatedBracket(listOf(regExp))
        var new = regExp
        for(rm in removeDoubleBracket){
            val size = rm.length
            val i = new.indexOf(rm)
            val loop = (findLoop(rm) && new.elementAt( i + size) != '^')
            val replace = rm.replace("(","").replace(")","").replace("^","")
            new = if(loop)
                new.replaceRange(i, i + size,"$replace^")
            else new.replaceRange(i, i + size, replace)
        }
        return new
    }

    private fun formattedAddOperations(regExp: String): String {
        var new = StringBuffer(regExp)
        var coeffLearnStr = 0
        for(i in regExp.indices) {
            val char = regExp[i]
            val nextChar = try { regExp[i + 1] } catch(c: Exception) { break }
            if(isClosing(char) && (isOpening(nextChar) || !isSpecial(nextChar))) {
                new.insert( coeffLearnStr+ i + 1 ,"*")
                coeffLearnStr ++
            }
            if(isLoop(char) && (isOpening(nextChar) || !isSpecial(nextChar))) {
                new.insert( coeffLearnStr+ i + 1 ,"*")
                coeffLearnStr ++
            }
            if(!isSpecial(char) && isOpening(nextChar)){
                new.insert( coeffLearnStr+ i + 1 ,"*")
                coeffLearnStr ++
            }
        }
        return new.toString()
    }

    private fun formattedRegexp(regExp: String): String {
        return removeDoubleBracket(formattedAddOperations(regExp))
    }

    private fun checkBracket(regExp: String): Boolean {
        val countBracket =  regExp.count { isBracket(it) }
        return (countBracket % 2) == 0
    }

    private fun correctPlacedOperations(regExp: String){
        for (i in regExp.indices){
            val char = regExp[i]
            val nextChar = try { regExp[i + 1] } catch(c: Exception) { break }
            if(isOpening(char) && isOperation(nextChar)) throw Exception( " Некоректно указан операнд: (+ | (*" ) // (+ | (*
            if(isOperation(char) && isClosing(nextChar)) throw Exception( " Некоректно указан операнд: +) | *)" ) // +) | *)
            if((isOperation(char) || isOpening(char)) && isLoop(nextChar)) throw Exception(" Некоректно указан операнд: +^ | (^ | *^" ) // +^ | (^ | *^
            if(isLoop(char) && isLoop(nextChar)) throw Exception(" Некоректно указан оператор: ^" ) // ^^
            if(isOperation(char) && isOperation(nextChar)) throw Exception(" Некоректно указан операнд: + | *" ) // ++ | ** | +* | *+

        }
    }

    fun analysis(regExp: String): String {
        if(!checkBracket(regExp))
            throw Exception( "пропущена скобка") //четность скобок
        val  withoutSpacesString = withoutSpaces(regExp)
        try { correctPlacedOperations(withoutSpacesString) }catch (e: Exception) { throw Exception( " Некоректно указан операнд" ) }
        return formattedRegexp(withoutSpacesString)
    }
}