package uz.uzkassa.smartpos.mappers;

import org.mapstruct.*;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.branch.BranchDTO;
import uz.uzkassa.smartpos.dto.branch.CabinetBranchCreateDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {CommonMapper.class})
public interface BranchMapper {
    @Mapping(target = "activityTypeId", source = "activityTypeDTO.id")
    @Mapping(target = "activityType", ignore = true)
    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "neighborhoodId", source = "neighborhood.id")
    @Mapping(target = "neighborhood", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "cashboxCreatedDate", ignore = true)
    @Mapping(target = "serialNumber", ignore = true)
    @Mapping(target = "ctoId", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "fiscal", ignore = true)
    @Mapping(target = "cityKeyId", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "devices", ignore = true)
    @Mapping(target = "ofdId", ignore = true)
    @Mapping(target = "ofdStatus", ignore = true)
    @Mapping(target = "rentFile", ignore = true)
    @Mapping(target = "cadastreFile", ignore = true)
    @Mapping(target = "managerId", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "mainBranch", ignore = true)
    @Mapping(target = "apayStatus", ignore = true)
    @Mapping(target = "phone", ignore = true)
    Branch toEntity(CabinetBranchCreateDto branchCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(CabinetBranchCreateDto branchCreateDto, @MappingTarget Branch branch);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "activityTypeDTO", source = "activityType")
    @Mapping(target = "rentFileUrl", source = "rentFile.url")
    @Mapping(target = "cadastreFileUrl", source = "cadastreFile.url")
    @Mapping(target = "ofdStatusName", source = "ofdStatus.nameRu")
    @Mapping(target = "usersCount", expression = "java(branch.getUsers().stream().filter(user -> !user.isDeleted()).count())")
    @Mapping(target = "deviceSerialNumbers", expression = "java(branch.getDevices().stream().filter(d->d.getType() == uz.uzkassa.smartpos.enums.DeviceType.FISCAL).map(terminal -> terminal.getSerialNumber()).toList())")
    @Mapping(target = "terminalSerialNumbers", expression = "java(branch.getDevices().stream().filter(d->d.getType() != uz.uzkassa.smartpos.enums.DeviceType.FISCAL).map(terminal -> terminal.getSerialNumber()).toList())")
    BranchDTO toDto(Branch branch);

}
