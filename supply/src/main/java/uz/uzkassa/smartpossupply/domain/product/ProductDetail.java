package uz.uzkassa.smartpossupply.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.Month;
import uz.uzkassa.smartpossupply.enums.TargetAudience;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_detail",
        indexes = {
                @Index(columnList = "product_id", name = "product_detail_product_id_idx"),
        })
public class ProductDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8958698475L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "width")
    Integer width;

    @Column(name = "height")
    Integer height;

    @Column(name = "length")
    Integer length;

    @Column(name = "package_width")
    Integer packageWidth;

    @Column(name = "package_height")
    Integer packageHeight;

    @Column(name = "package_length")
    Integer packageLength;

    @Column(name = "manufacturer")
    String manufacturer;

    @Column(name = "atricle_number")
    String articleNumber;

    @Column(name = "description", length = 2000)
    String description;

    @Column(name = "material")
    String material;

    @Column(name = "colour")
    String colour;

    @Column(name = "model")
    String model;

    @Column(name = "expire_date")
    LocalDateTime expireDate;

    @Column(name = "production_date")
    LocalDateTime productionDate;

    @Column(name = "composition")
    String composition;

    @Column(name = "recipe")
    String recipe;

    @Column(name = "purpose")
    String purpose;

    @Column(name = "age_from")
    Integer ageFrom;

    @Column(name = "age_to")
    Integer ageTo;

    @Column(name = "production_country_id")
    Long productionCountryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_country_id", updatable = false, insertable = false)
    Country productionCountry;

    @Column(name = "target_audience")
    @Enumerated(EnumType.STRING)
    TargetAudience targetAudience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seo_keywords", columnDefinition = "jsonb")
    Set<String> seoKeywords = new HashSet<>();

    @ElementCollection(targetClass = Month.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_seasonality", joinColumns = @JoinColumn(name = "product_detail_id", referencedColumnName = "id"))
    @Column(name = "month")
    Set<Month> seasonality = new HashSet<>();
}
