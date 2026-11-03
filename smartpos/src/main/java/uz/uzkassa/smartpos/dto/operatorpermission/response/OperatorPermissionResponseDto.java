package uz.uzkassa.smartpos.dto.operatorpermission.response;

import lombok.Data;
import uz.uzkassa.smartpos.domain.postgres.user.User;

import java.time.LocalDateTime;

@Data
public class OperatorPermissionResponseDto {
    private Long id;
    private boolean deleted = false;
    private String code;
    private String tin;
    private Long clientId;
    private Long companyId;
    private Long userId;
    private User user;
    private LocalDateTime expireDate;
}
