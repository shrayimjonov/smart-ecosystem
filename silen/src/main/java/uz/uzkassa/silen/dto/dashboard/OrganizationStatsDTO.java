package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrganizationStatsDTO implements Serializable {
    private String organizationId;
    private String organizationName;
    private ProductType productType;
    private String type;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal losses;
    private BigDecimal balance;
    private BigDecimal income;
    private List<StatsV2DTO> data;

    public OrganizationStatsDTO(String organizationId, String organizationName, List<StatsV2DTO> data) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.data = data;
    }

    public OrganizationStatsDTO(String organizationId, String organizationName, String type, BigDecimal production, BigDecimal shipment/*, BigDecimal losses*/) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.productType = StringUtils.isNotEmpty(type) ? ProductType.valueOf(type) : null;
        this.type = this.productType != null ? this.productType.getNameRu() : null;
        this.production = production;
        this.shipment = shipment;
        this.losses = BigDecimal.ZERO;
        this.balance = this.production.subtract(this.shipment).subtract(this.losses);
    }

    public StatsV2DTO getStats(StatsV2DTO sumDto){
        StatsV2DTO dto = new StatsV2DTO();
        dto.setProductType(getProductType());
        dto.setType(getType());
        dto.setProduction(getProduction());
        dto.setShipment(getShipment());
        dto.setLosses(getLosses());
        dto.setBalance(getBalance());
        return dto;
    }

    public List<StatsV2DTO> getData() {
        if(data == null){
            data = new ArrayList<>();
        }
        return data;
    }
}
