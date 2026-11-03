package uz.uzkassa.smartpossupply.domain.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsDescriptionDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsFieldDTO;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.enums.ProductGroup;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "generic_settings", indexes = {
    @Index(columnList = "settings_type", name = "generic_settings_settings_id_idx"),
    @Index(columnList = "settings_group_id", name = "generic_settings_settings_group_id_idx"),
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GenericSettings extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 865428942L;

    @Enumerated(EnumType.STRING)
    @Column(name = "settings_type", nullable = false)
    GenericSettingsType settingsType;

    @Column(name = "read_only", columnDefinition = "boolean default false")
    boolean readOnly = false;

    @Column(name = "enabled", columnDefinition = "boolean default false")
    boolean enabled = false;

    @Column(name = "for_cabinet", columnDefinition = "boolean default false")
    boolean forCabinet = false;

    @Column(name = "order_number")
    Integer orderNumber;

    @Column(name = "settings_group_id")
    Long settingsGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_group_id", updatable = false, insertable = false)
    SettingsGroup settingsGroup;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings_fields", columnDefinition = "jsonb")
    List<GenericSettingsFieldDTO> fields = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allowed_product_groups", columnDefinition = "jsonb")
    List<ProductGroup> allowedProductGroups = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description", columnDefinition = "jsonb")
    GenericSettingsDescriptionDTO description;

    public String getDescription(LanguageEnum language) {
        if (getDescription() != null) {
            return switch (language) {
                case uz -> getDescription().getDescriptionUz();
                case cyrillic -> getDescription().getDescriptionCyrillic();
                default -> getDescription().getDescriptionRu();
            };
        }
        return null;
    }
}
