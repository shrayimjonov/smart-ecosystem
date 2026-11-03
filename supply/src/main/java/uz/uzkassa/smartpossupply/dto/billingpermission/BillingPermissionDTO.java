package uz.uzkassa.smartpossupply.dto.billingpermission;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingPermissionDTO extends BillingSyncDTO {
    @Valid
    PermissionXizmatDTO xizmat;
    String xizmatType;
    String id;
    String status;
    Long agreementId;
    String calculatePer;

    @Override
    public String toString() {
        return "BillingPermissionDTO{" +
                "xizmat=" + xizmat +
                ", xizmatType='" + xizmatType + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", agreementId=" + agreementId +
                ", calculatePer='" + calculatePer + '\'' +
                ", parent=" + super.toString() +
                '}';
    }
}
