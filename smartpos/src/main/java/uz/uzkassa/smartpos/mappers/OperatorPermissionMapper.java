package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.user.OperatorPermission;
import uz.uzkassa.smartpos.dto.operatorpermission.response.OperatorPermissionResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OperatorPermissionMapper {
    @Mapping(target = "user", ignore = true)
    OperatorPermissionResponseDto toDto(OperatorPermission entity);
}
