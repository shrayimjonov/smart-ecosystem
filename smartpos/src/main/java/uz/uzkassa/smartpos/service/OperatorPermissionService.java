package uz.uzkassa.smartpos.service;


import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionActivateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionCreateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.response.OperatorPermissionResponseDto;

public interface OperatorPermissionService {

    OperatorPermissionResponseDto create(OperatorPermissionCreateRequestDto dto);

    OperatorPermissionResponseDto activate(OperatorPermissionActivateRequestDto dto);

    OperatorPermissionResponseDto getLastByCompanyId(Long id);

    OperatorPermissionResponseDto getOperatorPermissionsByCompanyId(Long companyId, Long userId);
}

