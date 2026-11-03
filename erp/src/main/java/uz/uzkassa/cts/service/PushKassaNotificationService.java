package uz.uzkassa.cts.service;

public interface PushKassaNotificationService {

    void sendNotificationToSession(String message, String serviceTag, String serviceKey, String sessionId);

    void sendNotificationToPublic(String message, String serviceTag, String serviceKey);
}
