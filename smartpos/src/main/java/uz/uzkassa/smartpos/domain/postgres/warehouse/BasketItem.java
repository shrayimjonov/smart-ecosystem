package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplacePhotoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
@Table(name = "basket_item", uniqueConstraints = {@UniqueConstraint(name = "basket_item_product_uk", columnNames = {"supplier_product_id", "company_id"})})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @NotNull
    @Column(name = "supplier_product_id")
    private Long supplierProductId;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;

    @NotNull
    private Long supplyId;
    @NotNull
    @Column(name = "supply_tin")
    private String supplyTin;
    @NotBlank
    private String supplyName;

    @Column(name = "supply_branch_id")
    private Long supplyBranchId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "barcode")
    private String barcode;

    @NotNull
    @Column(name = "vat_barcode")
    private String vatBarcode;

    @NotNull
    @Column(name = "unit_code")
    private Long unitCode;
    @NotNull
    private Long unitId;
    @NotBlank
    private String unitName;

    @JdbcTypeCode(SqlTypes.JSON)
    private ArrayList<MarketplacePhotoDTO> photos = new ArrayList<>();

    @NotNull
    @Column(name = "qty", scale = 4, precision = 25)
    private BigDecimal qty = BigDecimal.ONE;

    @NotNull
    @Column(name = "price", scale = 4, precision = 25)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "vat_rate", scale = 4, precision = 25)
    private BigDecimal vatRate;

    private boolean selected = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketItem that = (BasketItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
