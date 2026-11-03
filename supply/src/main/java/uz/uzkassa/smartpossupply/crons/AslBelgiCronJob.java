package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.CompanyAggregationInfoService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Profile("prod")
public class AslBelgiCronJob {

    CompanyAggregationInfoService companyAggregationInfoService;
    WarehouseProductService warehouseProductService;

    @Profile("prod")
    @Scheduled(fixedRate = 7200000) //2 hours
    public void refreshToken() {
        companyAggregationInfoService.refreshAslBelgiTokens();
    }

    @Profile("dev")
    @Scheduled(fixedRate = 7200000) //2 hours
    public void refreshTokenForDev() {
        companyAggregationInfoService.refreshAslBelgiTokensForDev();
    }

    @Profile("prod")
    @Scheduled(cron = "0 30 0 * * ?") // every day at 00:30
    public void batchUpdateCodeStatus() {
        warehouseProductService.batchUpdateCodeStatus();
    }
}
