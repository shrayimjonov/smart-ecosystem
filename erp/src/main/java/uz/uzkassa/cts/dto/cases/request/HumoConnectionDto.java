package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HumoConnectionDto {
    private String director;
    private String companyName;
    private String tin;
    private LocalDate date;
    private String phone;
    private String bankName;
    private String accountNumber;
    private String mfo;
    private String address;
    private String customerBranchAddress;
    private BigDecimal humoPercentage;
    private BigDecimal visaPercentage;
    private BigDecimal uzcardPercentage;
    private List<HumoConnectionDeviceDto> devices = new ArrayList<>();
    private String secretKey;
    private String deviceSerialNumber;
}
