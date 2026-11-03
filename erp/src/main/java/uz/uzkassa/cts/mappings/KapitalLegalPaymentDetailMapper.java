package uz.uzkassa.cts.mappings;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegalResponseDto;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KapitalLegalPaymentDetailMapper {

    KapitalLegalPaymentDetail toEntity(KapitalLegalResponseDto dto);

    @Mapping(source = "amount", target = "amount", qualifiedByName = "StringToBigDecimal")
    KapitalLegalResponseDto toDto(KapitalLegalPaymentDetail entity);

    @Named("StringToBigDecimal")
    default BigDecimal extractDoubleFromString(String amount) {
        if (StringUtils.isEmpty(amount))
            return BigDecimal.ZERO;
        return new BigDecimal(amount).divide(BigDecimal.valueOf(100));
    }
}
