package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.common.ApplicationRoute;
import uz.uzkassa.smartpos.dto.applicationroute.request.ApplicationRouteRequestDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRoutUserPermissions;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteListResponseDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ApplicationRouterMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    ApplicationRoute toEntity(ApplicationRouteRequestDto dto);

    @InheritConfiguration(name = "toEntity")
    void update(ApplicationRouteRequestDto dto, @MappingTarget ApplicationRoute entity);

    @Mapping(target = "parentAnnotationName", source = "parent.annotationName")
    @Mapping(target = "parentMenuName", source = "parent.menuName")
    @Mapping(target = "parentEndPoint", source = "parent.endPoint")
    ApplicationRouteResponseDto toDto(ApplicationRoute entity);

    @Mapping(target = "subordinates", source = "children")
    ApplicationRouteListResponseDto toListDto(ApplicationRoute entity);

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "allowed", ignore = true)
    ApplicationRoutUserPermissions toPermissionListDto(ApplicationRoute entity);
}
