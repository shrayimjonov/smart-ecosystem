package uz.uzkassa.smartpossupply.dto.aslbelgi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CisInfo implements Serializable {

    private String requestedCis;
    private String cis;
    private String gtin;
    private String productName;
    private Integer productGroupId;
    private String productGroup;
    private String brand;
    private LocalDateTime producedDate;
    private String packageType;
    private String ownerInn;
    private String ownerName;
    private TuronMarkStatus status;
    private String statusText;
    private String parent;
    private String producerInn;
    private String producerName;
    private String errorMessage;
    private String errorCode;
    private List<String> child;
    private Map<String, CisInfo> children;
    private String originalCode;
    private BigDecimal qty;
    private Long entityId;
    private String entityType;
    private String entityNumber;
    private LocalDateTime emissionDate;
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;

    private Map<String, List<Object>> codes;

    public String getStatusText() {
        if (getStatus() != null) {
            return getStatus().getText();
        }
        return null;
    }

    public String getGtin() {
        return gtin;
    }

    public String getErrorMessage() {
        if (this.errorMessage != null && !"".equals(this.errorMessage) && !"null".equals(this.errorMessage)) {
            return this.errorMessage;
        } else if (this.errorCode != null && !"".equals(this.errorCode) && !"null".equals(this.errorCode)) {
            return this.errorCode;
        } else if ("GTIN не найден".equals(gtin)) {
            return this.errorMessage = gtin;
        } else if (this.status != null && !(TuronMarkStatus.APPLIED.equals(this.status) || TuronMarkStatus.INTRODUCED.equals(this.status) || TuronMarkStatus.INTRODUCED_RETURNED.equals(this.status))) {
            return this.getStatusText();
        }
        return null;
    }


    public String getRequestedCis() {
        if (requestedCis == null || requestedCis.length() == 0) {
            return null;
        }
        String requestedCisPart = requestedCis;
        String part1 = requestedCis.substring(0, 4);
        if (part1.contains("(") && part1.contains(")")) {
            part1 = part1.replace("(", "").replace(")", "");
            requestedCisPart = part1.concat(requestedCisPart.substring(4));
        }
        return requestedCisPart;
    }

    public String getRequestedCisOriginal(ProductGroup productGroup) {
        if (ProductGroup.Tobacco.equals(productGroup) && requestedCis.length() > 25) {
            String requestedCisPart = requestedCis;
            String part1 = requestedCis.substring(0, requestedCis.length() - 7);
            if (part1.contains("(") && part1.contains(")")) {
                part1 = part1.replace("(", "").replace(")", "");
                requestedCisPart = part1.concat(requestedCisPart.substring(requestedCisPart.length() - 7));
            }
            return requestedCisPart;
        }
        return requestedCis;
    }

    public String getCode() {
        return Optional.ofNullable(Optional.ofNullable(this.getCis()).orElse(this.getRequestedCis())).orElse(this.getOriginalCode());
    }
}
