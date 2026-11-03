package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.common.Address;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.supply.dto.AddressDTO;
import uz.uzkassa.smartpos.integration.supply.dto.AddressDetailDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    Address toEntity(AddressDTO dto);

    @Mapping(target = "region.id", source = "regionId")
    @Mapping(target = "region.name", source = "regionName")
    @Mapping(target = "district.id", source = "districtId")
    @Mapping(target = "district.name", source = "districtName")
    AddressDetailDTO toDto(Address address);

    @Mapping(target = "name", source = "nameRu")
    CommonDTO toDto(Region region);

    @Mapping(target = "name", source = "nameRu")
    CommonDTO toDto(City city);
}
