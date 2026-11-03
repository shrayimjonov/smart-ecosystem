package uz.uzkassa.horeca.utils;

public class ReceiptUtils {

    public static String generateReceiptUid(Long userId) {
        return String.format("%d%d", System.currentTimeMillis(), userId);
    }
}
