package uz.uzkassa.smartpos.dto.catalog;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.math.BigDecimal;

@Getter
@Setter
public class TerminalProductDto {
    private Long id;
    private Long branchId;
    private boolean deleted;
    private boolean favorite;
    private boolean hasExcise;
    private boolean hasMark;
    private String barcode;
    private String vatBarcode;
    private BigDecimal exciseAmount;
    private BigDecimal salesPrice;
    private BigDecimal vatRate;
    private String name;
    private Long categoryId;
    private Long unitId;
    private String committentTin;
    private boolean creditEnabled;
    private CreditSettings creditSettings;
    private boolean advanceEnabled;
    private CreditSettings advanceSettings;
    private MxikPackage defaultPackage;
    private Integer origin;
}
