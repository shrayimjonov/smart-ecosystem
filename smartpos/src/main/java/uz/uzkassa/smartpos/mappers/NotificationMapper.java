package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.Notification;
import uz.uzkassa.smartpos.dto.notification.AdminNotificationDto;
import uz.uzkassa.smartpos.dto.notification.NotificationCreateDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CommonMapper.class})
public interface NotificationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedById", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "companies", ignore = true)
    @Mapping(target = "image", ignore = true)
    Notification toEntity(NotificationCreateDto notificationCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(NotificationCreateDto notificationCreateDto, @MappingTarget Notification notification);

    @Mapping(target = "read", ignore = true)
    AdminNotificationDto toAdminDto(Notification notification);
}