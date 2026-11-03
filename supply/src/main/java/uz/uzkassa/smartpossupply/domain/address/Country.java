package uz.uzkassa.smartpossupply.domain.address;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.Continent;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "country",
        indexes = {
                @Index(columnList = "name_ru", name = "country_name_ru_idx"),
                @Index(columnList = "name_uz", name = "country_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "country_name_cyrillic_idx"),
                @Index(columnList = "code", name = "country_code_idx"),
                @Index(columnList = "continent", name = "country_continent_idx")
        }
)
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Country extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 212513322L;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "continent", nullable = false)
    Continent continent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CommonStatus status = CommonStatus.ACTIVE;
}
