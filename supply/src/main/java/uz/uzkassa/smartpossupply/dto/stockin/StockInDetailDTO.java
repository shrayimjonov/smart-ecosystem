package uz.uzkassa.smartpossupply.dto.stockin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.GrandTotalDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInDetailDTO extends GrandTotalDTO {

    Long id;

    String stockNumber;

    StockStatus status;

    LocalDateTime transferDate;

    FacturaLinkDTO factura;

    CommonDTO party;

    CompanyBaseDTO supplier;

    CompanyCommittentLookUpDTO companyCommittent;

    CompanyBaseDTO customer;

    CommonDTO warehouse;

    CommonDTO branch;

    CommonDTO oldStock;

    CommonDTO stockOut;

    String description;

    CommonDTO stockTransfer;

    CommonDTO aggregation;

    AcceptanceTransferActLinkDTO acceptanceTransferAct;

    CommonDTO order;

    List<StockInProductDetailDTO> products = new ArrayList<>();

    boolean from1C;

    boolean fromHippo;

    CommonCodeDTO relation;

    boolean hasContract;

    CommonDTO aic;

    LocalDateTime lastModifiedDate;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    @JsonIgnore
    public void addProduct(StockInProductDetailDTO product) {
        products.add(product);
    }
}
