package com;

public class GenRegExp {
    private String substr;
    private String[] alf;
    private String simbl;
    private int k;

    public GenRegExp(String sequence, String[] alphabet, String symbol, int k) {
        this.substr = sequence;
        this.alf = alphabet;
        this.simbl = symbol;
        this.k = k;
    }

    public static String[] delItem(String[] data, String item) {
        String[] rez = new String[data.length - 1];
        int i = 0;
        for (String s : data)
            if (!s.equals(item))
                rez[i++] = s;
        return rez;
    }

    public static int countSymbol(String str, String symbol) {
        return str.length() - str.replaceAll(symbol, "").length();
    }

    public static String join(String[] data, String split) {
        StringBuilder rez = new StringBuilder("(");
        for (String s : data) {
            rez.append(s).append(split);
        }
        rez = new StringBuilder(rez.substring(0, rez.length() - 1));
        rez.append(")^");
        return rez.toString();
    }

    public static String blockBase(int k, String base, String symbol, boolean fl) {
        if (k == 0)
            return "";
        StringBuilder rez = new StringBuilder((fl) ? base + "(" : "(");
        for (int i = 0; i < k; i++) {
            rez.append(symbol).append(base);
        }
        rez.append(")");
        return rez.toString();
    }

    public String build() {
        String rez = "";
        String base = join(delItem(alf, simbl), "+");
        int addK = k - (countSymbol(substr, simbl) % k);
        int j = 0;
        if (k != 1) {
            if (substr.length() == 0) {
                rez += blockBase(k, base, simbl, true) + "^";
            } else {
                for (int i = 0; i <= addK; i++) {
                    j = addK - i;
                    rez += blockBase(i, base, simbl, false) + substr + blockBase(j, base, simbl, true) + "+";
                }
                rez = blockBase(k, base, simbl, true) + "^(" + rez.substring(0, rez.length() - 1) + ")" + blockBase(k, base, simbl, true) + "^";
            }
        } else {
            if (substr.length() != 0)
                rez += join(alf, "+") + substr + join(alf, "+");
            else
                rez += join(alf, "+");
        }
        return rez;
    }
}
