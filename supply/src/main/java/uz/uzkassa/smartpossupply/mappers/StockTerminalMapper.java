package uz.uzkassa.smartpossupply.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutDTO;

/**
 * User: Shuxratjon Rayimjonov
 * Date: 17.10.2024
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StockTerminalMapper {

    StockOutDTO toDTO(TerminalStockOutDTO dto);

}
