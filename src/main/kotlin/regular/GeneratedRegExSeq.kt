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
    private var alphabetWithoutMulti = ""

    private fun blockMulti() {
        alphabetWithoutMulti = alphabet.filter { it != symbolMulti.first }.joinToString("+")
        moduleMulti = "${symbolMulti.first}*($alphabetWithoutMulti)^"
        blockMulti += moduleMulti
        for (i in 1 until symbolMulti.second) blockMulti += "*$moduleMulti"
        blockMulti = "($alphabetWithoutMulti)^*($blockMulti)^"
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
            shemMulti +="+$subStart%&*$subEnd"
        }
    }

    private fun createdBodyMulti() = "(${shemMulti.replace("%", underSeq).replace("#", moduleMulti).replace("&", "($alphabetWithoutMulti)^")})"

    private fun existSymbolMutliInUnderSeq() = underSeq.split(symbolMulti.first).size - 1

    private fun multiOne(): String  =
        if(underSeq.contains(symbolMulti.first)) "(${alphabet.joinToString("+")})^*$underSeq*(${alphabet.joinToString("+")})^"
        else "(${alphabet.joinToString("+")})^*(${symbolMulti.first}*$underSeq+$underSeq*${symbolMulti.first})(${alphabet.joinToString("+")})^"

    private fun multiDouble(): String =
        if(existSymbolMutliInUnderSeq() % 2 == 0) startStringMulti(underSeq)
        else startStringMulti("($underSeq*($alphabetWithoutMulti)^*$moduleMulti*$moduleMulti+$moduleMulti*$moduleMulti*$underSeq)")

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
        if(underSeq.isBlank()) {
            if(symbolMulti.first.isBlank() || symbolMulti.second == 1) return "(${alphabet.joinToString("+")})^"
            blockMulti()
            return blockMulti
        }
        checkValidData()
        if(symbolMulti.first.isBlank()) return "(${alphabet.joinToString("+")})^*$underSeq*(${alphabet.joinToString("+")})^"
        if(symbolMulti.second == 1) return multiOne()
        blockMulti()
        if(symbolMulti.second == 2) return multiDouble()
        return multi()
    }

    private fun checkValidData() {
        val special = listOf("*", "(", ")", "+", "^", "#", "%")
        alphabet.forEach {
            if(special.contains(it))
                throw Exception("uncorrected alphabet")
        }

        underSeq.forEach {
            if (special.contains(it.toString()))
                throw Exception("uncorrected sequence")
        }
    }
}