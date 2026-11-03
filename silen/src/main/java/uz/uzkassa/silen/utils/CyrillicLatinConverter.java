package uz.uzkassa.silen.utils;

import java.util.HashMap;
import java.util.Map;

public class CyrillicLatinConverter {


    static char[] cyrilic = new char[]{
        '\u0410', '\u0430',    //A
        '\u0411', '\u0431',    //B
        '\u0412', '\u0432',    //V
        '\u0413', '\u0433',    //G
        '\u0414', '\u0434',    //D
        '\u0415', '\u0435',    //E
        '\u0401', '\u0451',    //Ё
        '\u0416', '\u0436',    //Ж
        '\u0417', '\u0437',    //З
        '\u0418', '\u0438',    //I
        '\u0419', '\u0439',    //Й y
        '\u041A', '\u043A',    //K
        '\u041B', '\u043B',    //L
        '\u041C', '\u043C',    //M
        '\u041D', '\u043D',    //N
        '\u041E', '\u043E',    //O
        '\u041F', '\u043F',    //P
        '\u0420', '\u0440',    //R
        '\u0421', '\u0441',    //C S
        '\u0422', '\u0442',    //T
        '\u0423', '\u0443',    //U
        '\u0424', '\u0444',    //F
        '\u0425', '\u0445',    //Х
        '\u0426', '\u0446',    //ц ts
        '\u0427', '\u0447',    //ч
        '\u0428', '\u0448',    //ш
        '\u0429', '\u0449',    //щ
        '\u042A', '\u044A',    //Ъ
        '\u042B', '\u044B',    //Ы
        '\u042C', '\u044C',    //Ь
        '\u042D', '\u044D',    //Э
        '\u042E', '\u044E',    //Ю
        '\u042F', '\u044F',    //Я
        '№', '№',    //?
        '\u041A', '\u043A',    //K
    };
    static String[] latin = new String[]{
        "A", "a",
        "B", "b",
        "V", "v",
        "G", "g",
        "D", "d",
        "E", "e",
        "Yo", "yo",
        "J", "j",
        "Z", "z",
        "I", "i",
        "Y", "y",
        "K", "k",
        "L", "l",
        "M", "m",
        "N", "n",
        "O", "o",
        "P", "p",
        "R", "r",
        "S", "s",
        "T", "t",
        "U", "u",
        "F", "f",
        "X", "x",
        "Ts", "ts",
        "CH", "ch",
        "Sh", "sh",
        "Sh", "sh",
        "", "",
        "I", "i",
        "", "",
        "E", "e",
        "Yu", "yu",
        "Ya", "ya",
        "", "",
        "Q", "q",
    };


    /**
     * Mapping of cyrillic characters to latin characters.
     */
    static Map<Character, String> cyrMapping = new HashMap<Character, String>();
    /**
     * Mapping of latin characters to cyrillic characters.
     */
    static Map<String, Character> latMapping = new HashMap<String, Character>();

    // Static initialization of mappings between cyrillic and latin letters.
    static {
        for (int i = 0; i < cyrilic.length; i++) {
            cyrMapping.put(cyrilic[i], latin[i]);
            latMapping.put(latin[i], cyrilic[i]);
        }
    }

    //*************************************************************************
    //*                            API methods                                *
    //*************************************************************************

    /**
     * Converts latin text to Serbian cyrillic
     *
     * @param latinText - Latin text to be converted to cyrillic.
     * @return - Serbian cyrillic representation of given latin text.
     */
    public static String latinToCyrillic(String latinText) {
        if (latinText == null) return "";
        StringBuffer latBuffer = new StringBuffer(latinText);
        StringBuffer cyrBuffer = new StringBuffer();

        for (int i = 0; i < latBuffer.length(); i++) {
            String s = latBuffer.substring(i, i + 1);
            if (i < latBuffer.length() - 1) {
                char c = latBuffer.charAt(i + 1);
                if (((s.equals("L") || s.equals("l")
                    || s.equals("N") || s.equals("n")) && (c == 'J' || c == 'j'))) {
                    s = s + 'j';
                    i++;
                } else if ((s.equals("D") || s.equals("d"))
                    && (c == '\u017D' || c == '\u017E')) {
                    s = s + '\u017E';
                    i++;
                }
            }
            if (latMapping.containsKey(s)) {
                cyrBuffer.append(latMapping.get(s).charValue());
            } else {
                cyrBuffer.append(s);
            }
        }
        return cyrBuffer.toString();
    }

    /**
     * Converts given Serbian cyrillic text to latin text.
     *
     * @param cyrillicText - Cyrillic text to be converted to latin text.
     * @return latin representation of given cyrillic text.
     */
    public static String cyrilicToLatin(String cyrillicText) {
        if (cyrillicText == null) return "";
        StringBuffer cyrBuffer = new StringBuffer(cyrillicText);
        StringBuffer latinBuffer = new StringBuffer();
        for (int i = 0; i < cyrBuffer.length(); i++) {
            char c = cyrBuffer.charAt(i);
            Character character = c;
            if (cyrMapping.containsKey(character)) {
                latinBuffer.append(cyrMapping.get(character));
            } else {
                latinBuffer.append(c);
            }
        }
        return latinBuffer.toString();
    }

}
