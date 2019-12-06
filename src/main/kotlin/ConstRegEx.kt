import regular.Reg

val REGEX1 = listOf(
    Reg(term = listOf("0")),
    Reg(andReg = listOf(
        Reg(term = listOf("0","1"), single = false),
        Reg(term = listOf("0","1"))
    ))
)
val REGEX2 = listOf(  // 0 * (0 + 1 + a)^ * 1
    Reg(term = listOf("0")),
    Reg(andReg = listOf(
        Reg(term = listOf("0","1","a"), single = false)
    )),
    Reg(term = listOf("1"))
)
val REGEX2_v2 = listOf(  // 0 * (0 + 1 + a)^ * 1
    Reg(term = listOf("0")),
    Reg(term = listOf("0","1","a"), single = false),
    Reg(term = listOf("1"))
)
val REGEX3 = listOf(  // (((00 + 11)^ + (01 + 10) * (00 + 11)^ + (01 + 10) * (00 + 11)^ ))^
    Reg(orReg = listOf(
        Reg(term = listOf("00","11"), single = false),
        Reg(andReg = listOf(Reg(term = listOf("01","10")),Reg(term = listOf("00","11"), single = false))),
        Reg(andReg = listOf(Reg(term = listOf("01","10")),Reg(term = listOf("00","11"), single = false)))
    ), single = false)
)
val REGEX4 = listOf(  // 0 * (0 + 1 + a)^ * ((0 + 1 + a) * (0 + 1 + a))^
    Reg(term = listOf("0")),
    Reg(andReg = listOf(
        Reg(term = listOf("0","1","a"), single = false)
    )),
    Reg(andReg = listOf(
        Reg(andReg = listOf(Reg(term = listOf("0","1","a")),Reg(term = listOf("0","1","a"))))
    ), single = false)
)
val REGEX5 = listOf(  // (0 + 1)^ * 00 * (0 + 1)^
    Reg(term = listOf("0", "1"), single = false),
    Reg(term = listOf("00")),
    Reg(term = listOf("0", "1"), single = false)
)
val REGEX6 = listOf(  // 0 * (0 + 1)^ * (00^ + (0 + 1))
    Reg(term = listOf("0")),
    Reg(term = listOf("0","1"), single = false),
    Reg(andReg = listOf(
        Reg(term = listOf("00"), single = false),
        Reg(term = listOf("0","1"))
    ), single = false)
)