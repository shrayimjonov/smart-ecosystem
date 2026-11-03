package uz.uzkassa.cts.dto.tin.integration;


import uz.uzkassa.cts.enums.BusinessType;

import java.math.BigDecimal;

public class OfdCheckTinResponseResultDTO extends OfdCheckTinResponseDataDTO {

    private Long regionId;

    private Long districtId;

    private BigDecimal balance;

    private BusinessType businessType;

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }
}
