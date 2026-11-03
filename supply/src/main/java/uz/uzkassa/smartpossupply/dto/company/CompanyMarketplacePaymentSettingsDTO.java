package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.08.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyMarketplacePaymentSettingsDTO {

    List<String> paymentTypes;

    boolean isConfirmed;
}
