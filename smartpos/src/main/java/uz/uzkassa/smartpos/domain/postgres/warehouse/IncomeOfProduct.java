package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.enums.IncomeOfProductStatus;
import uz.uzkassa.smartpos.enums.IncomeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "income_of_product",
        indexes = {
                @Index(columnList = "document_no", name = "income_of_product_document_idx"),
                @Index(columnList = "to_branch_id", name = "income_of_product_branch_idx")
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeOfProduct extends SimpleEntity {

    @Column(name = "document_no")
    String documentNo;

    @Column(name = "document_date")
    LocalDate documentDate;

    @Column(name = "income_date")
    LocalDateTime incomeDate;

    @Column(name = "description")
    String description;

    @Column(name = "supplier_id")
    Long supplierId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Company supplier;

    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Company customer;

    @NotNull
    @Column(name = "to_branch_id", nullable = false)
    Long toBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Branch toBranch;

    @OneToMany(
            mappedBy = "incomeOfProduct",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<IncomeOfProductDetail> incomeOfProductDetails = new ArrayList<>();

    @Column(name = "factura_id", unique = true)
    private String facturaId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", referencedColumnName = "factura_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Invoice invoice;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IncomeStatus status = IncomeStatus.SUCCESS;
    @Enumerated(EnumType.STRING)
    private IncomeOfProductStatus incomeStatus = IncomeOfProductStatus.DRAFT;
    @Column(name = "stock_out_id")
    private String stockOutId;

    public BigDecimal getTotalSumma() {
        return BigDecimal.valueOf(incomeOfProductDetails.stream()
                .filter(productDetail -> productDetail.getTotalSum() != null)
                .mapToDouble(productDetail -> productDetail.getTotalSum().doubleValue()).sum());
    }

}
