package uz.uzkassa.cts.service;

import uz.uzkassa.cts.enums.IntegrationType;

public interface TokenStoreService {

    String getToken(IntegrationType type);

    void create(IntegrationType type, String token, Long expiration);

    void delete(IntegrationType integrationType);
}
