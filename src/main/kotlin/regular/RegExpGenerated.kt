package regular

data class Reg(
    val term: List<String> = listOf(),
    val orReg: List<Reg> = listOf(),
    val andReg: List<Reg> = listOf(),
    val single: Boolean = true
)

class RegExpGenerated(regexp: List<Reg>) {
    private val rules = regexp

    private var listGeneratedString = listOf<String>()
    private var listChain = listOf<String>()


    private fun generatedString(chain: String, terms: List<String>, single: Boolean, maxLen: Int) {
        if (chain.count() > maxLen) return
        if (single){
            if( chain.isNotBlank() ) this.listGeneratedString += chain
        }else {
            this.listGeneratedString += chain
        }
        for (term in terms) {
            if(single){
                generatedString(chain + term, listOf(), single, maxLen)
            }else{
                generatedString(chain + term, terms, single, maxLen)
            }
        }
    }

    private fun merge(maxLen: Int) {
        if(this.listChain.isEmpty() || this.listGeneratedString.isEmpty()){
            this.listChain += this.listGeneratedString
            this.listGeneratedString = emptyList()
            return
        }
        val list = mutableListOf<String>()
        for (chain in this.listChain) {
            for (string in this.listGeneratedString) {
                val size = chain.length + string.length
                if (size <= maxLen) {
                    list += "$chain$string"
                }
            }
        }
        this.listChain = list.toList()
        this.listGeneratedString = emptyList()
    }

    private fun generated(chain: String, rule: Reg, maxLen: Int) {
        for (action in rule.andReg) {
            if(action.term.isNotEmpty()) {
                generatedString(chain, action.term, action.single, maxLen)
                merge(maxLen)
                continue
            }
            generated(chain, action, maxLen)
            merge(maxLen)
        }
    }

    private fun removalExcess(minLen: Int): List<String>{
        val list = this.listChain.filter { it.length >= minLen }.toSet()
        this.listChain = emptyList()
        return list.toList()
    }

    fun start(minLen: Int, maxLen: Int): List<String> {
        val startReg = Reg(andReg = rules)
        generated("", startReg, maxLen)
        println("====result=====")
        this.listChain.forEach { println(it) }
        return removalExcess(minLen)
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