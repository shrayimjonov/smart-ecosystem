package uz.uzkassa.smartpos.dto.elasticsearch;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CompanyES implements Serializable {
    private String inn;
    private String name;
    private String brand;
    private String oked;
    private String address;
    private String businessType;
    private boolean paysNds = true;
    private BigDecimal ndsPercent;
    private boolean warehouseEnabled = true;
    private boolean registered;
    private boolean fiscal;
    private LocalDateTime syncDate;
    private Long regionId;
    private RegionES region;
    private Long cityId;
    private CityES city;
    private Long ownerId;
    private UserES owner;
    private Long logoId;
    private Long companyBankId;
}
