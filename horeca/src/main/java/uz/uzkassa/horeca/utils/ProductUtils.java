package uz.uzkassa.horeca.utils;

public final class ProductUtils {

    public static String clearCatalogCode(String catalogCode) {
        return catalogCode.trim().substring(0, 8);
    }
}
