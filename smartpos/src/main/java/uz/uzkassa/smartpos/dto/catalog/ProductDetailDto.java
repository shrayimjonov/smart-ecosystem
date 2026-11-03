package uz.uzkassa.smartpos.dto.catalog;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.product.AttributeValue;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDto {
    private Long id;
    private Long branchId;
    private String name;
    private String barcode;
    private String vatBarcode;
    private String committentTin;
    private String description;
    private BigDecimal vatRate;
    private boolean hasMark;
    private boolean hasExcise;
    private boolean favorite;
    private BigDecimal exciseAmount;
    private UnitDTO unit;
    private CommonDTO category;
    private BigDecimal salesPrice;
    private boolean deleted;
    private List<ProductUnitDTO> units;
    private BigDecimal amount;
    private boolean creditEnabled;
    private CreditSettings creditSettings;
    private boolean advanceEnabled;
    private CreditSettings advanceSettings;
    private MxikPackage defaultPackage;
    private Short origin;
    private CommonFileDTO image;
    private HashSet<AttributeValue> attributes;

    public ProductDetailDto(ProductBranch product) {
        this.id = product.getProductId();
        this.branchId = product.getBranchId();
        this.name = product.getName();
        this.barcode = product.getBarcode();
        this.vatBarcode = product.getVatBarcode();
        this.committentTin = product.getCommittentTin();
        this.description = product.getDescription();
        this.vatRate = product.getVatRate();
        this.hasMark = product.isHasMark();
        this.hasExcise = product.isHasExcise();
        this.favorite = product.isFavorite();
        this.exciseAmount = product.getExciseAmount();
        if (product.getUnit() != null) {
            this.unit = product.getUnit().toDTO();
        }
        if (product.getCategory() != null) {
            this.category = new CommonDTO(product.getCategoryId(), product.getCategory().getName());
        }
        this.salesPrice = product.getSalesPrice();
        this.deleted = product.isDeleted();
        if (CollectionUtils.isNotEmpty(product.getUnits())) {
            this.units = product.getUnits()
                    .stream().map(productUnit -> new ProductUnitDTO(productUnit, salesPrice))
                    .toList();
        }
        this.creditEnabled = product.isCreditEnabled();
        this.creditSettings = product.getCreditSettings();
        this.advanceEnabled = product.isAdvanceEnabled();
        this.advanceSettings = product.getAdvanceSettings();
        this.defaultPackage = product.getDefaultPackage();
        this.origin = product.getOrigin();
        this.image = product.getImage();
        this.attributes = product.getAttributes();
    }
}
