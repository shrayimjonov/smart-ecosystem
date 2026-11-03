package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.RentOffer;
import uz.uzkassa.cts.dto.rentoffer.request.RentOfferRequestDto;
import uz.uzkassa.cts.dto.rentoffer.response.RentOfferResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentOfferMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "number", target = "contractNumber")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "branch.name", target = "branchName")
    RentOfferResponseDto toDto(RentOffer entity);

    RentOffer toEntity(RentOfferRequestDto dto);

    RentOffer toEntity(RentOfferRequestDto dto, @MappingTarget RentOffer entity);
}
