package uz.uzkassa.smartpossupply.dto.billingpermission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionDetailDTO {
    Long xizmatId;
    String title;
    String titleUz;
    String xizmatType;
    Long agreementId;
    String serviceType;
}
