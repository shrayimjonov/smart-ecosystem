package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Customer;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link Customer} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO implements Serializable {
    Long id;

    @NotBlank
    String tin;

    String name;

    boolean hasFinancialDiscount;

    boolean isCommittent;

    @Min(value = 0, message = "Комиссионный вознаграждения не может быт менше 0")
    @Max(value = 100, message = "Комиссионный вознаграждения не может быт болше 100")
    BigDecimal comission;

    String account;

    String address;

    String mobile;

    String workPhone;

    String oked;

    String director;

    String accountant;

    String districtId;

    String districtName;

    Long regionId;

    String regionName;

    String bankId;

    String bankName;
}
