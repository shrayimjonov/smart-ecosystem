package uz.uzkassa.cts.utils;

public final class WordUtils {

    public static String cyrillToLatin(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        text = text.replaceAll("[еэ]", "e");
        text = text.replaceAll("[ЕЭ]", "E");
        text = text.replaceAll("Ы", "I");
        text = text.replaceAll("ы", "i");

        text = text.replaceAll("а", "a");
        text = text.replaceAll("б", "b");
        text = text.replaceAll("ч", "ch");
        text = text.replaceAll("д", "d");
        text = text.replaceAll("ф", "f");
        text = text.replaceAll("г", "g");
        text = text.replaceAll("ғ", "g‘");
        text = text.replaceAll("ҳ", "h");
        text = text.replaceAll("и", "i");
        text = text.replaceAll("ж", "j");
        text = text.replaceAll("к", "k");
        text = text.replaceAll("қ", "q");
        text = text.replaceAll("л", "l");
        text = text.replaceAll("м", "m");
        text = text.replaceAll("н", "n");
        text = text.replaceAll("о", "o");
        text = text.replaceAll("ў", "o‘");
        text = text.replaceAll("п", "p");
        text = text.replaceAll("р", "r");
        text = text.replaceAll("с", "s");
        text = text.replaceAll("ш", "sh");
        text = text.replaceAll("т", "t");
        text = text.replaceAll("у", "u");
        text = text.replaceAll("в", "v");
        text = text.replaceAll("х", "x");
        text = text.replaceAll("й", "y");
        text = text.replaceAll("з", "z");
        text = text.replaceAll("ъ", "`");
        text = text.replaceAll("ь", "`");
        text = text.replaceAll("щ", "sh");
        text = text.replaceAll("я", "ya");
        text = text.replaceAll("ё", "yo");
        text = text.replaceAll("ю", "yu");
        text = text.replaceAll("ц", "s");

        text = text.replaceAll("А", "A");
        text = text.replaceAll("Б", "B");
        text = text.replaceAll("Ч", "Ch");
        text = text.replaceAll("Д", "D");
        text = text.replaceAll("Ф", "F");
        text = text.replaceAll("Г", "G");
        text = text.replaceAll("Ғ", "G‘");
        text = text.replaceAll("Ҳ", "H");
        text = text.replaceAll("И", "I");
        text = text.replaceAll("Ж", "J");
        text = text.replaceAll("К", "K");
        text = text.replaceAll("Қ", "Q");
        text = text.replaceAll("Л", "L");
        text = text.replaceAll("М", "M");
        text = text.replaceAll("Н", "N");
        text = text.replaceAll("О", "O");
        text = text.replaceAll("Ў", "O‘");
        text = text.replaceAll("П", "P");
        text = text.replaceAll("Р", "R");
        text = text.replaceAll("С", "S");
        text = text.replaceAll("Ш", "Sh");
        text = text.replaceAll("Т", "T");
        text = text.replaceAll("У", "U");
        text = text.replaceAll("В", "V");
        text = text.replaceAll("Х", "X");
        text = text.replaceAll("Й", "Y");
        text = text.replaceAll("З", "Z");
        text = text.replaceAll("Ъ", "`");
        text = text.replaceAll("Ь", "`");
        text = text.replaceAll("Я", "Ya");
        text = text.replaceAll("Ё", "Yo");
        text = text.replaceAll("Ю", "Yu");
        text = text.replaceAll("Ц", "S");
        text = text.replaceAll("Щ", "Sh");
        return text;
    }

    public static String latinToCyrill(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        text = text.replaceAll("[еэ]", "e");
        text = text.replaceAll("[ЕЭ]", "E");
        text = text.replaceAll("Ы", "I");
        text = text.replaceAll("ы", "i");

        text = text.replaceAll("А", "A");
        text = text.replaceAll("B", "Б");
        text = text.replaceAll("Ch", "Ч");
        text = text.replaceAll("Ya", "Я");
        text = text.replaceAll("Yo", "Ё");
        text = text.replaceAll("Yu", "Ю");
        text = text.replaceAll("D", "Д");
        text = text.replaceAll("F", "Ф");
        text = text.replaceAll("G`", "Ғ");
        text = text.replaceAll("G", "Г");
        text = text.replaceAll("H", "Ҳ");
        text = text.replaceAll("I", "И");
        text = text.replaceAll("J", "Ж");
        text = text.replaceAll("К", "K");
        text = text.replaceAll("Q", "Қ");
        text = text.replaceAll("L", "Л");
        text = text.replaceAll("M", "М");
        text = text.replaceAll("N", "Н");
        text = text.replaceAll("Oʻ", "Ў");
        text = text.replaceAll("O", "О");
        text = text.replaceAll("P", "П");
        text = text.replaceAll("R", "Р");
        text = text.replaceAll("C", "С");
        text = text.replaceAll("Sh", "Ш");
        text = text.replaceAll("T", "Т");
        text = text.replaceAll("U", "У");
        text = text.replaceAll("У", "Й");
        text = text.replaceAll("X", "Х");
        text = text.replaceAll("Y", "Й");
        text = text.replaceAll("Z", "З");
        text = text.replaceAll("`", "Ь");
        text = text.replaceAll("S", "C");

        text = text.replaceAll("a", "а");
        text = text.replaceAll("b", "б");
        text = text.replaceAll("sh", "ш");
        text = text.replaceAll("ch", "ч");
        text = text.replaceAll("d", "д");
        text = text.replaceAll("f", "ф");
        text = text.replaceAll("g`", "ғ");
        text = text.replaceAll("g", "г");
        text = text.replaceAll("h", "ҳ");
        text = text.replaceAll("i", "и");
        text = text.replaceAll("j", "ж");
        text = text.replaceAll("k", "к");
        text = text.replaceAll("q", "қ");
        text = text.replaceAll("l", "л");
        text = text.replaceAll("m", "м");
        text = text.replaceAll("n", "н");
        text = text.replaceAll("oʻ", "ў");
        text = text.replaceAll("o", "о");
        text = text.replaceAll("p", "п");
        text = text.replaceAll("r", "р");
        text = text.replaceAll("c", "с");
        text = text.replaceAll("t", "т");
        text = text.replaceAll("u", "у");
        text = text.replaceAll("v", "в");
        text = text.replaceAll("x", "х");
        text = text.replaceAll("y", "й");
        text = text.replaceAll("z", "з");
        text = text.replaceAll("`", "ъ");
        text = text.replaceAll("'", "ь");
        text = text.replaceAll("ya", "я");
        text = text.replaceAll("yo", "ё");
        text = text.replaceAll("yu", "ю");
        text = text.replaceAll("s", "c");
        return text;
    }
}
