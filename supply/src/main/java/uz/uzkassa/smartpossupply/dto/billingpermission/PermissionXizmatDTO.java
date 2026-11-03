package uz.uzkassa.smartpossupply.dto.billingpermission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionXizmatDTO {
    Long id;
    String title;
    String titleUz;
    String appType;
    String xizmatType;
}
