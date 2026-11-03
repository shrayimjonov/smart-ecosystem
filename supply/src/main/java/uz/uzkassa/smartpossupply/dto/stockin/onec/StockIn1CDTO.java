package uz.uzkassa.smartpossupply.dto.stockin.onec;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockCustomerDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockIn1CDTO extends GrandTotalDTO {

    Long id;

    CommonDTO warehouse;

    String stockNumber;

    CommonDTO branch;

    FacturaLinkDTO factura;

    CommonDTO party;

    CommonDTO aic;

    AcceptanceTransferActLinkDTO acceptanceTransferAct;

    StockCustomerDTO supplier;

    CompanyBaseDTO committent;

    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime transferDate;

    StockStatus status;

    boolean hasContract;

    List<StockInProduct1CDTO> products = new ArrayList<>();

    
    @JsonIgnore
    public void addProduct(StockInProduct1CDTO stockInProduct1CDTO) {
        products.add(stockInProduct1CDTO);
    }
}
