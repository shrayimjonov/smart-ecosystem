package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.domain.postgres.company.OneCCompany;
import uz.uzkassa.smartpos.dto.OneCCompanyDto;
import uz.uzkassa.smartpos.dto.terminal.company.TuronInfoDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressMapper.class)
public interface CompanyMapper {
    OneCCompanyDto toDto(OneCCompany oneCCompany);

    TuronInfoDto toDto(CompanyTuronInfo companyTuronInfo);
}
