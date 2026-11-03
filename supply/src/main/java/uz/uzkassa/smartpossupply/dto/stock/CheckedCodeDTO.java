package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckedCodeDTO implements Serializable {

    String code;

    String originalCode;

    String errorMessage;

    boolean checked;

    BigDecimal qty;

    CompanyBaseDTO owner;

    TuronMarkStatus status;

    PackageType packageType;

    public CheckedCodeDTO(String code, String originalCode, String errorMessage, BigDecimal qty, CompanyBaseDTO owner) {
        this(code, originalCode, errorMessage, false, qty, owner);
    }

    public CheckedCodeDTO(String code, String originalCode, String errorMessage, boolean checked, BigDecimal qty) {
        this.code = code;
        this.originalCode = originalCode;
        this.errorMessage = errorMessage;
        this.checked = checked;
        this.qty = qty;
    }

    public CheckedCodeDTO(String code, String originalCode, String errorMessage, boolean checked, BigDecimal qty, CompanyBaseDTO owner) {
        this.code = code;
        this.originalCode = originalCode;
        this.errorMessage = errorMessage;
        this.checked = checked;
        this.qty = qty;
        this.owner = owner;
    }

    public CheckedCodeDTO(String code, String originalCode, BigDecimal qty) {
        this.code = code;
        this.originalCode = originalCode;
        this.qty = qty;
    }
}
