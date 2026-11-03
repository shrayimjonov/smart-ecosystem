package uz.uzkassa.smartpossupply.dto.factura.supply;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.stock.StockCommonDTO;
import uz.uzkassa.smartpossupply.enums.FacturaType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SupplyFacturaListDTO implements Serializable {

    Long id;

    String facturaId;

    String facturaNo;

    LocalDate facturaDate;

    CompanyBaseDTO buyer;

    CompanyBaseDTO seller;

    Status status;

    String statusName;

    BigDecimal total;

    StockCommonDTO stock;

    @Deprecated
    StockStatus stockStatus;

    FacturaType facturaType;

    String contractNo;

    LocalDate contractDate;

    CommonDTO branch;

    LocalDateTime sendDate;
}
