package uz.uzkassa.smartpos.dto.operatorpermission.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OperatorPermissionActivateRequestDto {
    @NotNull
    private Long permissionId;
    @NotNull
    @NotEmpty
    private String code;
}
