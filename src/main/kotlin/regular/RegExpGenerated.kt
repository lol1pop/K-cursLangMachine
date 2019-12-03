package regular

data class Reg(
    val term: List<String> = listOf(),
    val termReg: List<Reg> = listOf(),
    val reg: List<Reg> = listOf(),
    val single: Boolean = true
)

class RegExpGenerated(regexp: List<Reg>) {
    private val rules = regexp

    private var listGeneratedString = listOf<String>()
    private var listChain = listOf<String>()


    private fun generatedString(chain: String, terms: List<String>, single: Boolean, maxLen: Int) {
        if (chain.count() > maxLen) return
        if (chain.isNotBlank())
        this.listGeneratedString += chain
        for (term in terms) {
            if(single){
                generatedString(chain + term, listOf(), single, maxLen)
            }else{
                generatedString(chain + term, terms, single, maxLen)
            }
        }
    }

    private fun generated(chain: String, rule: Reg, maxLen: Int) {
        for (action in rule.reg) {
            if(action.term.isNotEmpty()) {
                generatedString(chain, action.term, action.single, maxLen)
                this.listChain += this.listGeneratedString
                this.listGeneratedString = emptyList()
                continue
            }
            generated(chain, action, maxLen)
            this.listChain += this.listGeneratedString
            this.listGeneratedString = emptyList()
        }
    }

    fun start(minLen: Int, maxLen: Int): List<String> {
        val startReg = Reg(reg = rules)
        generated("", startReg, maxLen)
        return this.listChain
    }

}



/*
        print(chain)
        if (chain.count() > maxLen) return
        if (rule.term.isNotEmpty() && rule.term.first() == "end.") {
            if (chain.length >= minLen) {
                this.listGeneratedString += chain
                println(chain)
            }
            return
        }

        for (action in rule.term) {
            if (rule.single) {
                generated(chain + action, Reg(), minLen, maxLen)
                for (actionRul in rule.reg) {
                    generated(chain, actionRul, minLen, maxLen)
                }
            } else {
                generated(chain + action, rule, minLen, maxLen)
                for (actionRul in rule.reg) {
                    generated(chain, actionRul, minLen, maxLen)
                }
            }
        }

        for (actionRul in rule.reg) {
            generated(chain, actionRul, minLen, maxLen)
        }

        for (action in rules) {
           generated(chain, action, minLen, maxLen)
       }
 */