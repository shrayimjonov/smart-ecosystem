package uz.uzkassa.cts.dto.sale.act;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SaleActResponseDTO implements Serializable {

    private Long id;
    private String number;
    private LocalDateTime createdDate;
    private Long creatorId;
    private String creatorName;
    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.CREATED;

    private BigDecimal amount;
    private String accessoryNames;
    private BigInteger sumQty;

    private String fileUri;
    private String fileName;
    private String contentType;
    private String powerAttorneyFileUri;
    private String powerAttorneyFileName;
    private String powerAttorneyContentType;

    public String getNumber() {
        return getId() != null ? PrefixEnum.AAS + "/" + getId() : null;
    }
}
