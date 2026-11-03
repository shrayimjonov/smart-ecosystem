package uz.uzkassa.horeca.dto.billing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.enums.billing.BillingTariffType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class XizmatResponseDTO implements Serializable {

    private Long id;

    private boolean individual;
    private boolean displayInCabinet;

    @NotNull
    @NotEmpty
    private String title;

    private String xfileTitle;

    private String description;

    private String serviceType;

    private BigDecimal fee;
    private BigDecimal calculatedFee;

    private BigDecimal bonusAmount;

    private Integer trialCount;


    private Boolean recurring = false;


    private Integer sortOrder;

    private Boolean disabled = false;

    private Integer activeCount;

    private String appType;

    private String code;

    private String catalogCode;

    private String catalogName;

    private String packageName;

    private String packageCode;

    private Long lgotaId;
    private Integer lgotaType;
    private String lgotaName;


    private Boolean enableForSingle;


    private boolean bcv;
    private BigDecimal bcvInstance;
    private Integer freeUser;

    private BillingTariffType tariffType;

    public Boolean isRecurring() {
        return getRecurring();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XizmatResponseDTO)) {
            return false;
        }

        return id != null && id.equals(((XizmatResponseDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "XizmatDTO{" +
                "id=" + getId() +
                ", title='" + getTitle() + "'" +
                ", description='" + getDescription() + "'" +
                ", serviceType='" + getServiceType() + "'" +
                ", fee=" + getFee() +
                ", bonusAmount=" + getBonusAmount() +
                ", recurring='" + isRecurring() + "'" +
                ", trialCount=" + getTrialCount() +
                ", disabled='" + getDisabled() + "'" +
                "}";
    }
}

