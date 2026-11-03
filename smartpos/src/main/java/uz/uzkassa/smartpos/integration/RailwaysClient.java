package uz.uzkassa.smartpos.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uz.uzkassa.smartpos.config.FeignPropagateBadRequestsConfiguration;
import uz.uzkassa.smartpos.dto.sales.TransactionRailwaysPayload;

@FeignClient(name = "Railways", url = "${application.railways.host}", configuration = FeignPropagateBadRequestsConfiguration.class)
public interface RailwaysClient {
    @PostMapping("/api/v1/uzkassa/confirm-payment")
    void callback(@RequestHeader("secret-key") String secretKey, @RequestBody TransactionRailwaysPayload payload);
}
