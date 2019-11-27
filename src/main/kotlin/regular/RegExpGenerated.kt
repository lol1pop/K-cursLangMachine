package -+regular

data class Reg(
    val term: List<String> = listOf(),
    val reg: List<Reg> = listOf()
)

class RegExpGenerated(regexp: List<Reg>) {
    private val rules = regexp

    private var listGeneratedString = listOf<String>()


    private fun generated(chain: String, rule: Reg, minLen: Int, maxLen: Int) {
        if (chain.count() > maxLen) return
        if (rule.term.isNotEmpty() && rule.term.first() == "end.") {
            if (chain.length >= minLen) {
                this.listGeneratedString += chain
                println(chain)
            }
            return
        }
        for(action in rule.term){
            generated(chain + action, rule, minLen, maxLen)

        for(action in rule.reg){
            generated(chain , action, minLen, maxLen)
        }
        
        for(action in rules){
            generated(chain , action, minLen, maxLen)
        }
    }

    fun start (minLen: Int, maxLen: Int): List<String> {
        val startReg = Reg(reg = rules + Reg(term = listOf("end.")))
        generated("", startReg, minLen, maxLen)
        return this.listGeneratedString
    }

}