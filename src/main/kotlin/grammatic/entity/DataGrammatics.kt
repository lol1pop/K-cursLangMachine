package grammatic.entity

data class DataGrammatics(
    val nonterminalSymbols : String,
    val terminalSymbols: String,
    val rulesSymbol: Map<Char,List<String>>,
    val originSymbol: String
)
