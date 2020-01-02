package regular

class GenExp(private val substr: String,
             private val alphabet: Array<String?>,
             private val symbol: String,
             private val k: Int) {

    fun build(): String {
        var result = ""
        val base: String = merge(delItem(alphabet, symbol), "+")
        val symbolMulti: Int = k - countSymbol(substr, symbol) % k
        var j = 0
        if (k != 1) {
            if (substr.length == 0) {
                result += blockBase(k, base, symbol, true) + "^"
            } else {
                for (i in 0..symbolMulti) {
                    j = symbolMulti - i
                    result += blockBase(
                        i,
                        base,
                        symbol,
                        false
                    ) + substr + blockBase(j, base, symbol, true) + "+"
                }
                result = blockBase(k, base, symbol, true) + "^(" + result.substring(
                    0,
                    result.length - 1
                ) + ")" + blockBase(k, base, symbol, true) + "^"
            }
        } else {
            if (substr.length != 0) result += merge(
                alphabet,
                "+"
            ) + substr + merge(alphabet, "+") else result += merge(alphabet, "+")
        }
        return result
    }

    companion object {
        fun delItem(data: Array<String?>, item: String): Array<String?> {
            val rez = arrayOfNulls<String>(data.size - 1)
            var i = 0
            for (s in data) if (s != item) rez[i++] = s
            return rez
        }

        fun countSymbol(str: String, simbol: String): Int {
            return str.length - str.replace(simbol.toRegex(), "").length
        }

        fun merge(data: Array<String?>, split: String): String {
            var rez = "("
            for (s in data) {
                rez += s + split
            }
            rez = rez.substring(0, rez.length - 1)
            rez += ")^"
            return rez
        }

        fun blockBase(k: Int, base: String, simbl: String, fl: Boolean): String {
            if (k == 0) return ""
            var rez = if (fl) "$base(" else "("
            for (i in 0 until k) {
                rez += simbl + base
            }
            rez += ")"
            return rez
        }
    }

}