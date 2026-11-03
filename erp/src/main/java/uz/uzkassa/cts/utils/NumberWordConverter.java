package uz.uzkassa.cts.utils;

public class NumberWordConverter {
    public static final String[] units = {
        "", "один", "два", "три", "четыре", "пять", "шесть", "семь",
        "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
        "четырнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    public static final String[] tens = {
        "",        // 0
        "",        // 1
        "двадцать",  // 2
        "тридцать",  // 3
        "сорок",   // 4
        "пятьдесят",   // 5
        "шестьдесят",   // 6
        "семьдесят", // 7
        "восемьдесят",  // 8
        "девяносто"   // 9
    };

    public static String convert(final int n) {
        if (n < 0) {
            return "minus " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " сто" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 1000000) {
            return convert(n / 1000) + " тысяча" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 1000000000) {
            return convert(n / 1000000) + " миллион" + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
        }

        return convert(n / 1000000000) + " миллиард"  + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
    }

}
