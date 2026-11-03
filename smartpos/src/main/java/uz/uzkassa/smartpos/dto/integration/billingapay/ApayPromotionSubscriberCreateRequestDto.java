package uz.uzkassa.smartpos.dto.integration.billingapay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApayPromotionSubscriberCreateRequestDto {
    private String tin;
    private String apayPromotionId;
}

