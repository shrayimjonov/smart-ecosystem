package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TariffMetadata {
    private List<TariffPartnerService> partnerServicesList;
}
