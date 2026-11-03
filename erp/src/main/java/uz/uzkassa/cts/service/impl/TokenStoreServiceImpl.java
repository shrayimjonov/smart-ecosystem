package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.redis.TokenStore;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.repository.TokenStoreRepository;
import uz.uzkassa.cts.service.TokenStoreService;

import static lombok.AccessLevel.PRIVATE;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenStoreServiceImpl implements TokenStoreService {

    TokenStoreRepository tokenStoreRepository;

    public String getToken(IntegrationType type) {
        return tokenStoreRepository.findById(type).map(TokenStore::getToken).orElse(null);
    }

    @Override
    @Transactional
    public void create(IntegrationType type, String token, Long expiration) {
        tokenStoreRepository.save(TokenStore
                .builder()
                .type(type)
                .token(token)
                .expiration(expiration)
                .build());
    }

    @Override
    @Transactional
    public void delete(IntegrationType integrationType) {
        tokenStoreRepository.deleteById(integrationType);
    }
}
