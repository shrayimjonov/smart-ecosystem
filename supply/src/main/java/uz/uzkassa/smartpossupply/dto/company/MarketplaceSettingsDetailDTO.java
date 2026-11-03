package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.08.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MarketplaceSettingsDetailDTO {

    Set<PaymentType> paymentTypes = new LinkedHashSet<>();

    CompanyDeliveryTypeDetailDTO deliveryType;

    MarketplaceSettingsStep step;

    public void add(PaymentType paymentTypeDTO) {
        this.paymentTypes.add(paymentTypeDTO);
    }
}
