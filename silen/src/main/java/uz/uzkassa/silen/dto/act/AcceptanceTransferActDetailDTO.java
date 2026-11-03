package uz.uzkassa.silen.dto.act;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.StoreDTO;
import uz.uzkassa.silen.dto.invoice.FacturaCustomer;
import uz.uzkassa.silen.dto.warehouse.CustomerDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActDetailDTO {

    String id;

    String actId;

    String actNumber;

    LocalDate actDate;

    LocalDateTime createdDate;

    List<AcceptanceTransferActProductSilenDTO> products = new LinkedList<>();

    String description;

    CustomerDTO customer;

    StoreDTO store;

    FacturaCustomer supplier;

    SelectItem contract;

    SelectItem status;

    BigDecimal comission;

    BigDecimal totalCost;

    BigDecimal totalDiscount;

    BigDecimal totalPayment;

    Long supplierBranchId;

    public void addProducts(AcceptanceTransferActProductSilenDTO productDTO) {
        this.products.add(productDTO);
    }
}
