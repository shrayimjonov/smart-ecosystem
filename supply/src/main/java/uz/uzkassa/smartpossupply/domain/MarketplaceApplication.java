package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationAction;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "marketplace_application",
    indexes = {
        @Index(columnList = "product_id", name = "marketplace_application_product_id_idx"),
        @Index(columnList = "marketplace_id", name = "marketplace_application_marketplace_id_idx"),
        @Index(columnList = "deleted", name = "marketplace_application_deleted_idx")
    })
@SQLDelete(sql = "UPDATE marketplace_application SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class MarketplaceApplication extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 3849598349L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "catalog_price", scale = 2, precision = 25)
    BigDecimal catalogPrice;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name", length = 2000)
    String catalogName;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "package_name", length = 2000)
    String packageName;

    @Column(name = "vat")
    @Enumerated(value = EnumType.STRING)
    Vat vat = Vat.WITHOUT_VAT;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "description_uz", length = 2000)
    String descriptionUz;

    @Column(name = "description_cyrillic", length = 2000)
    String descriptionCyrillic;

    @Column(name = "description_ru", length = 2000)
    String descriptionRu;

    @Column(name = "country_id")
    Long countryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", updatable = false, insertable = false)
    Country country;

    @Column(name = "marketplace_id")
    Long marketplaceId;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    MarketplaceApplicationStatus status = MarketplaceApplicationStatus.NOT_FILLED;

    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    MarketplaceApplicationAction action;

    @Column(name = "rejected_reason", length = 500)
    String rejectedReason;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();
}
