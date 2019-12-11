package regular

import java.lang.Exception

class GeneratedRegExSeq(
    val alphabet: List<String>,
    val underSeq: String,
    val symbolMulti: Pair<String, Int>
    ) {

    private var blockMulti = ""
    private var shemMulti = ""
    private var moduleMulti = ""

    private fun blockMulti() {
        val alphabetWithoutMulti = alphabet.filter { it != symbolMulti.first }.joinToString("+")
        moduleMulti = "${symbolMulti.first}*($alphabetWithoutMulti)^"
        blockMulti += moduleMulti
        for (i in 1 until symbolMulti.second) blockMulti += "*$moduleMulti"
        blockMulti = "($blockMulti)^"
    }

    private fun startStringMulti(block: String): String = "$blockMulti*$block*$blockMulti"

    private fun generatedShemMulti(size: Int) {
        var shema = ""
        for (i in 0 until size) shema += "#*"
        shema = shema.dropLast(1)
        shemMulti = "$shema*%"
        for (i in shema.indices step 2){
            val subStart = shema.subSequence(0, i)
            val subEnd = shema.subSequence(i, shema.length)
            shemMulti +="+$subStart%*$subEnd"
        }
    }

    private fun createdBodyMulti() = "(${shemMulti.replace("%", underSeq).replace("#", moduleMulti)})"

    private fun existSymbolMutliInUnderSeq() = underSeq.split(symbolMulti.first).size - 1

    private fun multiOne(): String  =
        if(underSeq.contains(symbolMulti.first)) "(${alphabet.joinToString("+")})^$underSeq(${alphabet.joinToString("+")})^"
        else "(${alphabet.joinToString("+")})^(${symbolMulti.first}*$underSeq+$underSeq*${symbolMulti.first})(${alphabet.joinToString("+")})^"

    private fun multiDouble(): String =
        if(existSymbolMutliInUnderSeq() % 2 == 0) startStringMulti(underSeq)
        else startStringMulti("($moduleMulti*$moduleMulti*$underSeq+$moduleMulti*$underSeq*$moduleMulti+$underSeq*$moduleMulti*$moduleMulti)")

    private fun multi(): String {
        val count = existSymbolMutliInUnderSeq()
        if(count == 0 || count > symbolMulti.second ){
            if(count % symbolMulti.second == 0){
                return startStringMulti(underSeq)
            }else{
                generatedShemMulti(count - symbolMulti.second)
                return startStringMulti(createdBodyMulti())
            }
        } else {
            generatedShemMulti(symbolMulti.second - count)
            return startStringMulti(createdBodyMulti())
        }
    }

    fun build(): String {
        checkValidData()
        if(symbolMulti.second == 1) return multiOne()
        blockMulti()
        if(symbolMulti.second == 2) return multiDouble()
        return multi()
    }

    private fun checkValidData() {
        val special = listOf("*", "(", ")", "+", "^", "#", "%")
        alphabet.forEach {
            if(special.contains(it))
                throw Exception()
        }

        if(symbolMulti.second <= 0)
            throw Exception()

        if(!alphabet.contains(symbolMulti.first))
            throw Exception()

        underSeq.forEach {
            if (special.contains(it.toString()))
                throw Exception()
        }
    }
}