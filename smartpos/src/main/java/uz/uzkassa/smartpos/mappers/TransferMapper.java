package uz.uzkassa.smartpos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Transfer;
import uz.uzkassa.smartpos.domain.postgres.warehouse.TransferDetail;
import uz.uzkassa.smartpos.dto.warehouse.TransferDetailDtoV2;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferDetailFashionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferFashionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferListDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR, uses = CommonMapper.class)
public interface TransferMapper {
    TransferFashionDto<TransferDetailFashionDto> toFashionDto(Transfer transfer);

    TransferFashionDto<TransferDetailDtoV2> toDto(Transfer transfer);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "colorCode", source = "productItem.colorGroup.colorCode")
    @Mapping(target = "colorName", source = "productItem.colorGroup.colorName")
    @Mapping(target = "printName", source = "productItem.colorGroup.printName")
    @Mapping(target = "sizeCode", source = "productItem.sizeCode")
    @Mapping(target = "sizeValue", source = "productItem.sizeValue")
    TransferDetailFashionDto toFashionDto(TransferDetail transferDetail);

    @Mapping(target = "productName", source = "product.name")
    TransferDetailDtoV2 toDto(TransferDetail transferDetail);

    TransferListDto toListDto(Transfer transfer);
}
