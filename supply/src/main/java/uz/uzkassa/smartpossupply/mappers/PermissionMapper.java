package uz.uzkassa.smartpossupply.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpossupply.domain.permission.Permission;
import uz.uzkassa.smartpossupply.dto.permission.PermissionDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends BaseMapper<Permission, PermissionDTO, PermissionDTO> {
}
