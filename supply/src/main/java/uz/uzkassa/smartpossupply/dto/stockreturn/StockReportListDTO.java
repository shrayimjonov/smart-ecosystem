package uz.uzkassa.smartpossupply.dto.stockreturn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stockreport.StockRevenueReport;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReportListDTO implements Serializable {

    List<StockRevenueReport> products = new ArrayList<>();

    BigDecimal totalQty;

    BigDecimal totalPrice;

    BigDecimal totalCostPrice;

    BigDecimal totalSalesPrice;

    
    @JsonIgnore
    public void addProduct(StockRevenueReport product) {
        products.add(product);
    }
}
