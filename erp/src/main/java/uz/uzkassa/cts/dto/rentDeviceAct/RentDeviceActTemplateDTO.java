package uz.uzkassa.cts.dto.rentDeviceAct;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentDeviceActTemplateDTO implements Serializable {
    private String modelName;
    private String serialNumber;
    private String companyName;
    private BigDecimal price;
    private String owner;
    private String login;
    private String address;
    private LocalDateTime createdDate;

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
