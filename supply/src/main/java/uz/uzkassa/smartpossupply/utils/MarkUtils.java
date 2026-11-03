package uz.uzkassa.smartpossupply.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MarkUtils {

    public static final DecimalFormat GTIN_FORMAT = new DecimalFormat("00000000000000");

    public static final HashMap<String, ProductGroup> vatBarcodeProductGroupMap = new HashMap<>();

    static {
        //   Ром
        vatBarcodeProductGroupMap.put("02208007", ProductGroup.Alcohol);
        //        Мескаль
        vatBarcodeProductGroupMap.put("02208014", ProductGroup.Alcohol);
        //        Этил спирти ва бошқа спиртли дамламалар
        vatBarcodeProductGroupMap.put("02207002", ProductGroup.Alcohol);
        //        Вижилловчи винолар
        vatBarcodeProductGroupMap.put("02204002", ProductGroup.Alcohol);
        //        Ликер
        vatBarcodeProductGroupMap.put("02208006", ProductGroup.Alcohol);
        //        Коньяк
        vatBarcodeProductGroupMap.put("02208002", ProductGroup.Alcohol);
        //        80 % ва ундан кўп градусли спиртли консентрацияли денатурацияланмаган этил спирти
        vatBarcodeProductGroupMap.put("02207001", ProductGroup.Alcohol);
        //        Джин
        vatBarcodeProductGroupMap.put("02208008", ProductGroup.Alcohol);
        //        Ароқ
        vatBarcodeProductGroupMap.put("02208001", ProductGroup.Alcohol);
        //        Алкоголсиз ичимликлар
        vatBarcodeProductGroupMap.put("02299001", ProductGroup.Water);
        //        Текила
        vatBarcodeProductGroupMap.put("02208010", ProductGroup.Alcohol);
        //        Самбука
        vatBarcodeProductGroupMap.put("02208012", ProductGroup.Alcohol);
        //        Вино
        vatBarcodeProductGroupMap.put("02204001", ProductGroup.Alcohol);
        //        Бальзам
        vatBarcodeProductGroupMap.put("02208003", ProductGroup.Alcohol);
        //        Виски
        vatBarcodeProductGroupMap.put("02208005", ProductGroup.Alcohol);
        //        Жилваланадиган
        vatBarcodeProductGroupMap.put("02206001", ProductGroup.Alcohol);
        //        Бренди
        vatBarcodeProductGroupMap.put("02208004", ProductGroup.Alcohol);
        //        Бошқа спиртли ичимликлар
        vatBarcodeProductGroupMap.put("02208015", ProductGroup.Alcohol);
        //        Вермут
        vatBarcodeProductGroupMap.put("02205001", ProductGroup.Alcohol);
        //        Спиртли дамламалар
        vatBarcodeProductGroupMap.put("02208013", ProductGroup.Alcohol);
        //        Жилваланмайдиган
        vatBarcodeProductGroupMap.put("02206002", ProductGroup.Alcohol);

        //        Пиво
        vatBarcodeProductGroupMap.put("02203001", ProductGroup.Beer);

        //        Шакар ва бошқа ширинлаштирувчи қўшимчали ёки лаззат берувчи моддаларни ўзи ичига олган сув
        vatBarcodeProductGroupMap.put("02202005", ProductGroup.Water);
        //        Газланган сув
        vatBarcodeProductGroupMap.put("02202001", ProductGroup.Water);
        //        Салқинлантирилган ичимликлар
        vatBarcodeProductGroupMap.put("02201003", ProductGroup.Water);
        //        Энергетик ичимликлар
        vatBarcodeProductGroupMap.put("02202003", ProductGroup.Water);
        //        Минерал сув (шакарсиз)
        vatBarcodeProductGroupMap.put("02201002", ProductGroup.Water);
        //        Таббий сув (шакарсиз)
        vatBarcodeProductGroupMap.put("02201001", ProductGroup.Water);
        //        Ичимликлар
        vatBarcodeProductGroupMap.put("02202002", ProductGroup.Water);
        //        Бошқалар
        vatBarcodeProductGroupMap.put("02202004", ProductGroup.Water);
        //        КАРКАДЕ чой ичимлиги
        vatBarcodeProductGroupMap.put("02106004", ProductGroup.Water);
        //        Қуруқ сут ичимлиги
        vatBarcodeProductGroupMap.put("02106001", ProductGroup.Water);
        //        Денгиз суви
        vatBarcodeProductGroupMap.put("02501003", ProductGroup.Water);
        //        Компот не содержащие добавок сахара или других подслащивающих веществ или спирта
        vatBarcodeProductGroupMap.put("02008002", ProductGroup.Water);

        //        Ажралмаган ўрта қовурғали тамаки
        vatBarcodeProductGroupMap.put("02401001", ProductGroup.Tobacco);
        //        Бошқа ишлаб чиқарилган табаклар
        vatBarcodeProductGroupMap.put("02403001", ProductGroup.Tobacco);
        //        Исталган нисбатда тамаки ўрнини босувчи моддаларни ўз ичига олган ёки олмаган чекиладиган тамаки
        vatBarcodeProductGroupMap.put("02403002", ProductGroup.Tobacco);
        //        Таркибида тамаки бор сигареталар
        vatBarcodeProductGroupMap.put("02402001", ProductGroup.Tobacco);
        //        "Таркибида тамаки бўлган сигаралар, кесилган сигаралар ва сигариллалар"
        vatBarcodeProductGroupMap.put("02402002", ProductGroup.Tobacco);
        //        Электрон сигареталар, тамакини қиздириш мосламалари
        vatBarcodeProductGroupMap.put("08543999", ProductGroup.Tobacco);
        vatBarcodeProductGroupMap.put("03824999", ProductGroup.Tobacco);

        // ---- Группа 031 «УДОБРЕНИЯ» ----
        vatBarcodeProductGroupMap.put("03101001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03102001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03102002", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03102003", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03102999", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03103001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03104001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03104999", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03105001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03105002", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("03105003", ProductGroup.Fertilizers);
        // ---- Группа 038 «Средства защиты растений» ----
        vatBarcodeProductGroupMap.put("03808001", ProductGroup.Fertilizers);
        // ---- Группы 025 / 028 / 029 — общая химия ----
        vatBarcodeProductGroupMap.put("02510002", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02810001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02814001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02827001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02833001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02834001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02835001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02840001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02841001", ProductGroup.Fertilizers);
        vatBarcodeProductGroupMap.put("02903001", ProductGroup.Fertilizers);
    }

    public static boolean validateCode(String code, PackageType packageType) {
        if (packageType == null) {
            return isMark(code);
        } else if (PackageType.BOX.equals(packageType)) {
            return code.matches("^[(]?[0]?[0]?[)]?[0]{1}.{17}$");
        } else if (PackageType.PALLET.equals(packageType)) {
            return code.matches("^[(]?[0]?[0]?[)]?[1]{1}.{17}$");
        }
        return false;
    }

    public static boolean isMark(String code) {
        return (
                alcoholMatcher(code) ||
                        alcoholBlockMatcher(code) ||
                        tobaccoBlockMatcher(code) ||
                        tobaccoMatcher(code) ||
                        appliancesMatcher(code) ||
                        pharmaMatcher(code) ||
                        waterMatcher(code)
        );
    }

    public static boolean blockMatcher(String code) {
        return alcoholBlockMatcher(code) || tobaccoBlockMatcher(code);
    }

    public static boolean alcoholBlockMatcher(String code) {
        return code.matches("^01[0-9]{14}21(.{13})[↔\u001D\u001D]?[\u001d\u001d]?93.{4}$");
    }


    public static boolean tobaccoBlockMatcher(String code) {
        return code.matches("^(01|\\(01\\))([0-9]{14})(21|\\(21\\)).{7}[↔\u001D\u001D]?(93.{8})?$");
    }

    private static boolean appliancesMatcher(String code) {
        return code.matches("^.{38}[↔\u001D\u001D]?[9]{1}[1]{1}.{4}[↔\u001D\u001D]?[9]{1}[2]{1}.{44}$");
    }

    private static boolean pharmaMatcher(String code) {
        return code.matches("^.{31}[↔\u001D\u001D]?[9]{1}[1-3]{1}.{4}[↔\u001D\u001D]?[9]{1}[2]{1}.{44}$");
    }

    public static boolean tobaccoMatcher(String code) {
        return code.matches("^[0-9]{14}.{15}$");
    }

    private static boolean alcoholMatcher(String code) {
        return code.matches("^01([0-9]{14})21(.{7})[↔\u001D\u001D]?[\u001d\u001d]?93.{4}$");
    }

    private static boolean waterMatcher(String code) {
        return code.matches("^.{31}[↔\u001D\u001D]?[9]{1}[3]{1}.{4}$");
    }

    public static boolean isAggregation(String code) {
        return code.matches("^[(]?[0]?[0]?[)]?[0-9].{17}$");
    }

    public static String clearMarkCigaretteBlock(String code) {
        return code.substring(0, 25);
    }

    public static boolean alcoholMatcherWithoutCX(String code) {
        return code.matches("^.{25}$");
    }

    //(01)0487021676250621D1p
    public static String clearAggregationCode(String code) {
        if (code.contains("(") && code.contains(")")) {
            code = code.replace("(", "");
            code = code.replace(")", "");
        }
        return code;
    }

    /// "(01)04870216762506(21)D1p"vlw"
    /// "010487021676250621D1p"vlw"
    /// 00030635156500415558
    public static String clearOriginalCode(String code, ProductType productType) {
        if (productType != null) {
            if (ProductGroup.Tobacco.equals(productType.getProductGroup())) {
                if (code.contains("(") && code.contains(")") && code.startsWith("(00)")) {
                    code = code.replace("(", "");
                    code = code.replace(")", "");
                }
            } else if (ProductGroup.Alcohol.equals(productType.getProductGroup()) || ProductGroup.Pharma.equals(productType.getProductGroup())) {
                if (code.contains("(") && code.contains(")") && code.startsWith("(00)")) {
                    code = code.substring("(00)".length());
                }
            }
        }
        if (productType != null && ProductGroup.Fertilizers.equals(productType.getProductGroup()) && code.length() >= 31) {
            return code.substring(0, 31);
        }

        if (code.contains("\u001D")) {
            code = code.substring(0, code.indexOf("\u001D"));
        } else if (code.contains("\u001d")) {
            code = code.substring(0, code.indexOf("\u001d"));
        } else if (code.contains("")) {
            code = code.substring(0, code.indexOf(""));
        } else if (code.contains("[GS]")) {
            code = code.substring(0, code.indexOf("[GS]"));
        } else if (code.contains("↔")) {
            code = code.substring(0, code.indexOf("↔"));
        }


        if ((productType != null && !ProductGroup.Pharma.equals(productType.getProductGroup())) && !ProductGroup.Water.equals(productType.getProductGroup()) && !ProductGroup.Fertilizers.equals(productType.getProductGroup()) && !ProductType.AlcoholBlock.equals(productType) && alcoholMatcher(code)) {
            return code.substring(0, 25);
        } else if (tobaccoBlockMatcher(code)) {
            return code.substring(0, 25);
        }
        if ((productType != null && !ProductGroup.Pharma.equals(productType.getProductGroup())) && alcoholBlockMatcher(code)) {
            return code.substring(0, 31);
        } else if (pharmaMatcher(code) || waterMatcher(code)) {
            return code.substring(0, 31);
        } else if (appliancesMatcher(code)) {
            return code.substring(0, 38);
        } else if (isAggregation(code) && code.length() > 18 && productType == null) {
            return code.substring(code.length() - 18);
        }
        else if (tobaccoMatcher(code)) {
            return code.substring(0, 21);
        } else {
            return code;
        }
    }

    public static String addZerosToAggregationCode(String code) {
        if (code.length() == 18) {
            code = "00" + code;
        }
        return code;
    }

    public static String generateGTIN(String barcode) {
        if (StringUtils.isNotEmpty(barcode) && barcode.length() < 14) {
            StringBuilder builder = new StringBuilder();
            int remaining = 14 - barcode.length();
            builder.append("0".repeat(remaining));
            return StringUtils.join(builder.toString(), barcode);
        }
        return barcode;
    }

    public static String generateControlNumber(String number) {
        int result = 0;
        int oddSum = 0;
        int evenSum = 0;
        int x = 1;
        for (int i = number.length() - 1; i >= 0; i--) {
            int a = Character.getNumericValue(number.charAt(i));
            if (x % 2 == 0) {
                evenSum += a;
            } else {
                oddSum += a;
            }
            x++;
        }
        int sum = oddSum * 3 + evenSum;
        if (sum % 10 != 0) {
            result = 10 - sum % 10;
        }
        return String.valueOf(result);
    }

    public static boolean checkIsEqualBarcodes(String barcode, String mark) {
        if (StringUtils.isAnyBlank(barcode, mark)) {
            return false;
        }
        return mark.contains(barcode);
    }

    public static ProductType getProductType(ProductGroup productGroup, String vatBarcode) {
        if (productGroup != null) {
            return ProductType.buildByMarkingCode(productGroup.getMarkingCode());
        }
        if (StringUtils.isNotEmpty(vatBarcode)) {
            productGroup = vatBarcodeProductGroupMap.get(clearVatBarcode(vatBarcode));
            if (productGroup != null) {
                return ProductType.buildByMarkingCode(productGroup.getMarkingCode());
            }
        }
        return null;
    }

    public static String clearVatBarcode(String vatBarcode) {
        return vatBarcode.trim().substring(0, 8);
    }

    public static String getBarcodeFromMarkCode(String markCode) {
        String clearMarkCode = markCode;
        if (markCode.startsWith("01")) {
            clearMarkCode = markCode.substring(2);
        } else if (markCode.startsWith("0")) {
            clearMarkCode = markCode.substring(1);
        }
        return clearMarkCode.substring(0, 14);
    }

    public static PackageType findPackageType(CisInfo cisInfo) {
        if (StringUtils.isNotEmpty(cisInfo.getPackageType())) {
            return PackageType.valueOf(cisInfo.getPackageType());
        }
        if (StringUtils.isNotEmpty(cisInfo.getOriginalCode()) && NumberUtils.isParsable(cisInfo.getOriginalCode())) {
            return PackageType.BOX;
        }
        List<String> codes = null;
        if (!CollectionUtils.isEmpty(cisInfo.getChild())) {
            codes = cisInfo.getChild();
        } else if (!org.springframework.util.CollectionUtils.isEmpty(cisInfo.getCodes())) {
            codes = new ArrayList<>(cisInfo.getCodes().keySet());
        }
        if (!CollectionUtils.isEmpty(codes)) {
            String originalCode = codes.get(0);
            if (StringUtils.isNotEmpty(originalCode)) {
                String code = originalCode.replace("(", "");
                code = code.replace(")", "");
                if (NumberUtils.isParsable(code)) {
                    return PackageType.PALLET;
                }
            }
        } else {
            return PackageType.UNIT;
        }
        return PackageType.BLOCK;
    }

    public static PackageType findPackageTypeByCode(String code, ProductType productType) {
        if (ProductGroup.Tobacco.equals(productType.getProductGroup())) {
            if (code.length() == 25) {
                return PackageType.BLOCK;
            } else if (code.contains("(") && code.contains(")")) {
                code = code.replace("(", "");
                code = code.replace(")", "");
                if (code.length() == 25) {
                    return PackageType.BLOCK;
                }
            } else if (code.length() == 29) {
                return PackageType.UNIT;
            }
        }
        if (ProductGroup.Alcohol.equals(productType.getProductGroup()) || ProductGroup.Beer.equals(productType.getProductGroup())) {
            if (code.length() == 31) {
                return PackageType.BLOCK;
            } else if (code.length() == 25) {
                return PackageType.UNIT;
            }
        }
        if (isAggregation(code)) {
            return PackageType.BOX;
        } else if (isBlock(code)) {
            return PackageType.BLOCK;
        }
        return PackageType.UNIT;
    }

    public static boolean isBlock(String code) {
        return blockMatcher(code);
    }
}
