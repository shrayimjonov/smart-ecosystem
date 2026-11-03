package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;


@Getter
@Setter
@NoArgsConstructor
public class MapStatsDTO implements Serializable {
    private Long cityCode;
    private String cityName;
    private Long regionCode;
    private String regionName;
    private Long receiptCount;
    private Long branchCount;
    private Long companyCount;
    private Long productCount;
    private BigDecimal totalCost;
    private BigDecimal totalNds;

    public MapStatsDTO(Long cityCode, String cityName, Long regionCode, String regionName,
                       BigInteger receiptCount, BigInteger branchCount, BigInteger companyCount) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.receiptCount = receiptCount != null ? receiptCount.longValue() : 0L;
        this.branchCount = branchCount != null ? branchCount.longValue() : 0L;
        this.companyCount = companyCount != null ? companyCount.longValue() : 0L;
    }
}
