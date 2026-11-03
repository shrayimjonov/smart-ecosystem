package uz.uzkassa.horeca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.permission.Permission;
import uz.uzkassa.horeca.dto.permission.PermissionDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends BaseMapper<Permission, PermissionDTO, PermissionDTO> {}
