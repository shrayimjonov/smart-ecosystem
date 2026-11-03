package uz.uzkassa.smartpos.dto.catalog;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ProductReceiptDTO {
    private String marking;
    private Long productId;
    private Long branchId;
    private BigDecimal vatPercent;
    private BigDecimal exciseAmount;
    private BigDecimal price;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private String vatBarcode;
    private String name;
    private String unitName;
    private String committentTin;
    private Long unitId;
    private Set<String> children;
    private BigDecimal amount;
    private MxikPackage defaultPackage;
    private boolean creditEnabled;
    private CreditSettings creditSettings;
    private boolean advanceEnabled;
    private CreditSettings advanceSettings;
}
