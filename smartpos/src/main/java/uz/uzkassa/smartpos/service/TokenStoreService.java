package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.redis.TokenStoreHash;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.integration.cto.CtoClient;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.repository.redis.TokenStoreRepository;

import java.util.Optional;

import static uz.uzkassa.smartpos.security.TokenUtils.getExpirationInSeconds;

@Service
@RequiredArgsConstructor
public class TokenStoreService {
    private final ApplicationProperties applicationProperties;
    private final TokenStoreRepository repository;
    private final SupplyClient supplyClient;
    private final CtoClient ctoClient;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final ApayClient apayClient;

    public String getToken(TokenType type) {
        Optional<TokenStoreHash> optionalToken = repository.findById(type);
        if (optionalToken.isPresent()) {
            return optionalToken.get().getValue();
        } else {
            return switch (type) {
                case TokenType.APAY -> {
                    String token = apayClient.getToken(applicationProperties.getApay().getUsername(), applicationProperties.getApay().getPassword());
                    repository.save(TokenStoreHash.builder()
                            .type(TokenType.APAY)
                            .value("Bearer " + token)
                            .expirationInSeconds(getExpirationInSeconds(token))
                            .build());
                    yield "Bearer " + token;
                }
                case TokenType.SUPPLY -> {
                    String token = supplyClient.getToken(applicationProperties.getSupply().getUsername(), applicationProperties.getSupply().getPassword());
                    repository.save(TokenStoreHash.builder()
                            .type(TokenType.SUPPLY)
                            .value("Bearer " + token)
                            .expirationInSeconds(getExpirationInSeconds(token))
                            .build());
                    yield "Bearer " + token;
                }
                case TokenType.CTS -> {
                    String token = ctoClient.getToken(applicationProperties.getCto().getUsername(), applicationProperties.getCto().getPassword());
                    repository.save(TokenStoreHash.builder()
                            .type(TokenType.CTS)
                            .value("Bearer " + token)
                            .expirationInSeconds(getExpirationInSeconds(token))
                            .build());
                    yield "Bearer " + token;
                }
                case TokenType.BILLING -> {
                    String token = billingServiceFeignClient.getToken(applicationProperties.getBilling().getUsername(), applicationProperties.getBilling().getPassword());
                    repository.save(TokenStoreHash.builder()
                            .type(TokenType.BILLING)
                            .value("Bearer " + token)
                            .expirationInSeconds(getExpirationInSeconds(token))
                            .build());
                    yield "Bearer " + token;
                }
            };
        }
    }
}
