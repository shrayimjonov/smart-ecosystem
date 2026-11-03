package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.BankTariff;
import uz.uzkassa.cts.dto.banktariff.BankTariffDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankTariffMapper {

    BankTariff toEntity(BankTariffDTO dto);

    default BankTariffDetailDTO toDetailDTO(BankTariff entity) {
        return BankTariffDetailDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .xizmat(new CommonDTO(entity.getXizmatId(), entity.getXizmatName()))
                .humoPercentage(entity.getHumoPercentage())
                .visaPercentage(entity.getVisaPercentage())
                .visaDomesticPercentage(entity.getVisaDomesticPercentage())
                .uzcardPercentage(entity.getUzcardPercentage())
                .appType(entity.getAppType())
                .bankType(entity.getBankType())
                .build();
    }
}

