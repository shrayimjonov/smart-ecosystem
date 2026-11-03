package uz.uzkassa.smartpossupply.dto.stockreturn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.12.2021 11:35
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnDetailDTO extends GrandTotalDTO {

    Long id;

    String stockNumber;

    StockStatus status;

    LocalDateTime transferDate;

    FacturaLinkDTO factura;

    CompanyBaseDTO customer;

    CommonDTO warehouse;

    CommonDTO branch;

    String description;

    List<StockReturnProductDetailDTO> products = new ArrayList<>();

    ContractLookUpDTO contract;

    boolean hasContract;

    LocalDateTime lastModifiedDate;

    @JsonIgnore
    public void addProduct(StockReturnProductDetailDTO product) {
        products.add(product);
    }
}
