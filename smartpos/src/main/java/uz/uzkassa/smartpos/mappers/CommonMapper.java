package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonMapper {
    @Mapping(target = "name", source = "number")
    CommonDTO toDto(Invoice invoice);

    CommonDTO toDto(Unit unit);

    CommonDTO toDto(Branch branch);

    @Mapping(target = "tin", source = "inn")
    CompanyBaseDTO toBaseDto(Company company);

    CommonDTO toDto(Company company);

    CommonDTO toDto(User user);

    CommonDTO toDto(Device device);

    RegionDTO toDto(Region region);

    @Mapping(target = "regionId", ignore = true)
    CityDTO toDto(City city);

    NeighborhoodDTO toDto(Neighborhood neighborhood);

    @Mapping(target = "name", source = "nameRu")
    ActivityTypeDTO toDto(ActivityType activityType);

    @Mapping(target = "fullName", source = "fullNameStr")
    UserShortDto toShortDto(User user);

    CommonFileDTO toDto(FileUpload fileUpload);

    @Mapping(target = "id", source = "productId")
    CommonDTO toDto(ProductBranch productBranch);
}
