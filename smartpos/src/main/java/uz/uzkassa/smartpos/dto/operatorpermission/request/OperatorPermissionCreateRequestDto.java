package uz.uzkassa.smartpos.dto.operatorpermission.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.uzkassa.smartpos.dto.operatorpermission.MessageSendingTypeEnum;

import java.io.Serializable;

@Data
public class OperatorPermissionCreateRequestDto implements Serializable {
    @NotNull
    @NotEmpty
    private String phoneNumber;
    @NotNull
    private Long companyId;
    @NotNull
    @NotEmpty
    private MessageSendingTypeEnum typeEnum;
}
