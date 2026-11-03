package uz.uzkassa.cts.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class NumberToWordUtil {


    public static String digits2Text(BigDecimal amount) {
        boolean stripkop = false;
        String[][] sex = {
            {"", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
            {"", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
        };
        String[] str100 = {"", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};
        String[] str11 = {"", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать", "двадцать"};
        String[] str10 = {"", "десять", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};
        String[][] forms = {
//            {"тийина", "тийинов", "тийин", "1"},
//            {"сум", "сума", "сумов", "0"},
//            {"тысяча", "тысячи", "тысяч", "1"},
//            {"миллион", "миллиона", "миллионов", "0"},
//            {"миллиард","миллиарда","миллиардов","0"},
//            {"триллион","триллиона","триллионов","0"},
            {"тийин", "тийин", "тийин", "1"},
            {"сум", "сум", "сум", "0"},
            {"тысяч", "тысяч", "тысяч", "1"},
            {"миллион", "миллион", "миллион", "0"},
            {"миллиард", "миллиард", "миллиард", "0"},
            {"триллион", "триллион", "триллион", "0"},
            // можно добавлять дальше секстиллионы и т.д.
        };
        // получаем отдельно рубли и копейки
        long rub = amount.longValue();
        String[] moi = amount.toString().split("\\.");
        long kop = Long.parseLong(moi[1]);
        if (!moi[1].startsWith("0")) {// начинается не с нуля
            if (kop < 10)
                kop *= 10;
        }
        String kops = String.valueOf(kop);
        if (kops.length() == 1)
            kops = "0" + kops;
        long rub_tmp = rub;
        // Разбиватель суммы на сегменты по 3 цифры с конца
        ArrayList<Long> segments = new ArrayList<>();
        while (rub_tmp > 999) {
            long seg = rub_tmp / 1000;
            segments.add(rub_tmp - (seg * 1000));
            rub_tmp = seg;
        }
        segments.add(rub_tmp);
        Collections.reverse(segments);
        // Анализируем сегменты
        StringBuilder o = new StringBuilder();
        if (rub == 0) {// если Ноль
            o = new StringBuilder("ноль " + morph(0, forms[1][0], forms[1][1], forms[1][2]));
            return o + " " + kop + " " + morph(kop, forms[0][0], forms[0][1], forms[0][2]);
        }
        // Больше нуля
        int lev = segments.size();
        for (Object segment : segments) {// перебираем сегменты
            int sexi = Integer.parseInt(forms[lev][3]);// определяем род
            int ri = Integer.parseInt(segment.toString());// текущий сегмент
            if (ri == 0 && lev > 1) {// если сегмент ==0 И не последний уровень(там Units)
                lev--;
                continue;
            }
            String rs = String.valueOf(ri); // число в строку
            // нормализация
            if (rs.length() == 1)
                rs = "00" + rs;// два нулика в префикс?
            if (rs.length() == 2)
                rs = "0" + rs; // или лучше один?
            // получаем циферки для анализа
            int r1 = Integer.parseInt(rs.substring(0, 1)); //первая цифра
            int r2 = Integer.parseInt(rs.substring(1, 2)); //вторая
            int r3 = Integer.parseInt(rs.substring(2, 3)); //третья
            int r22 = Integer.parseInt(rs.substring(1, 3)); //вторая и третья
            // Супер-нано-анализатор циферок
            if (ri > 99)
                o.append(str100[r1]).append(" "); // Сотни
            if (r22 > 20) {// >20
                o.append(str10[r2]).append(" ");
                o.append(sex[sexi][r3]).append(" ");
            } else { // <=20
                if (r22 > 9)
                    o.append(str11[r22 - 9]).append(" "); // 10-20
                else
                    o.append(sex[sexi][r3]).append(" "); // 0-9
            }
            // Единицы измерения (рубли...)
            o.append(morph(ri, forms[lev][0], forms[lev][1], forms[lev][2])).append(" ");
            lev--;
        }
        // Копейки в цифровом виде
        if (!stripkop) {
            o.append(kops).append(" ").append(morph(kop, forms[0][0], forms[0][1], forms[0][2]));
        }
        o = new StringBuilder(o.toString().replaceAll(" {2,}", " "));
        return o.toString();
    }

    /**
     * Склоняем словоформу
     *
     * @param n  Long количество объектов
     * @param f1 String вариант словоформы для одного объекта
     * @param f2 String вариант словоформы для двух объектов
     * @param f5 String вариант словоформы для пяти объектов
     * @return String правильный вариант словоформы для указанного количества объектов
     */
    public static String morph(long n, String f1, String f2, String f5) {
        n = Math.abs(n) % 100;
        long n1 = n % 10;
        if (n > 10 && n < 20)
            return f5;
        if (n1 > 1 && n1 < 5)
            return f2;
        if (n1 == 1)
            return f1;
        return f5;
    }






    //////////////////////////////////////////////////////////
    //число с единицами
    public static String numberToWords(int number, String word, String wordMore)
    {
        //получаем число прописью
        String str = numPhrase(number, true) + " ";

        //добавляем единицы
        byte endpart = (byte)(number % 100);
        if (endpart > 19)
            endpart = (byte)(endpart % 10);
        if (endpart == 1)
            str += word;
        else
            str += wordMore;

        return str;
    }

    static String numPhrase(int number, boolean isMale)
    {
        if (number == 0)
            return "нуля";
        String[] Dek1 = {
            "", " одно", " двух", " трех", " четырех", " пяти", " шести", " семи", " восеми", " девяти", " десяти", " одиннадцати",
            " двенадцати", " тринадцати", " четырнадцати", " пятнадцати", " шестнадцати", " семнадцати", " восемнадцати", " девятнадцати"
        };
        String[] Dek2 = {"", "", " двадцати", " тридцати", " сорока", " пятидесяти", " шестидесяти", " семидесяти", " восьмидесяти", " девяноста"};
        String[] Dek3 = {"", " ста", " двухсот", " трехсот", " четырехсот", " пятисот", " шестисот", " семисот", " восьмисот", " девятисот"};
        String[] Th = {"", "", " тысяч", " миллиона", " миллиарда", " триллиона", " квадрилиона", " квинтилиона"};
        StringBuilder str = new StringBuilder();
        for (byte th = 1; number > 0; th++)
        {
            short gr = (short)(number % 1000);
            number = (number - gr) / 1000;
            if (gr > 0)
            {
                byte d3 = (byte)((gr - gr % 100) / 100);
                byte d1 = (byte)(gr % 10);
                byte d2 = (byte)((gr - d3 * 100 - d1) / 10);
                if (d2 == 1)
                    d1 += (byte)10;
                boolean ismale = (th > 2) || (th == 1 && isMale);
                str.insert(0, Dek3[d3] + Dek2[d2] + Dek1[d1] + endDek1(d1, ismale) + Th[th] + endTh(th, d1));
            }
        }
        return str.toString();
    }

    static String endDek1(byte dek, boolean isMale)
    {
        if (dek == 1)
            return isMale ? "го" : "й";
        else
            return "";
    }

    static String endTh(byte thNum, byte dek)
    {
        if (thNum == 2 && dek == 1)                // одной тысячИ
            return "и";
        else if (thNum > 2 && dek > 1)            // двух миллионОВ, миллиардОВ
            return "ов";
        else
            return "";
    }


    public static void main(String[] args) {
        String s = numberToWords(360, "fg", "");
        System.out.println(s);
    }


}
