package uz.uzkassa.horeca.dto.factura.supply;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.enums.FacturaType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SupplyFacturaListDTO implements Serializable {

    Long id;

    String facturaId;

    String facturaNo;

    LocalDate facturaDate;

    CompanyCommonDTO buyer;

    CompanyCommonDTO seller;

    Status status;

    String statusName;

    BigDecimal total;

    CommonDTO stock;

    FacturaType facturaType;
}
