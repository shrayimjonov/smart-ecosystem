package uz.uzkassa.smartpos.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;

/**
 * Mapper for the entity {@link TelegramAccount} and its DTO {@link TelegramAccountDTO}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TelegramAccountMapper {

    TelegramAccountDTO toDto(TelegramAccount telegramAccount);

    @Mapping(target = "enabled", source = "enabled", ignore = true)
    @Mapping(target = "confirmed", source = "confirmed", ignore = true)
    @Mapping(target = "branches", ignore = true)
    @Mapping(target="createdBy", ignore = true)
            @Mapping(target="createdDate", ignore = true)
    @Mapping(target="lastModifiedBy", ignore = true)
            @Mapping(target="lastModifiedDate", ignore = true)
    @Mapping(target="agreementName", ignore = true)
            @Mapping(target="user", ignore = true)
    @Mapping(target="company", ignore = true)
    TelegramAccount toEntity(TelegramAccountDTO telegramAccountDTO);

}
