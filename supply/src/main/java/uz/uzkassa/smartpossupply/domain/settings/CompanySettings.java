package uz.uzkassa.smartpossupply.domain.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_settings", indexes = {
        @Index(columnList = "settings_id", name = "company_settings_settings_id_idx"),
        @Index(columnList = "company_id", name = "company_settings_company_id_idx"),
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CompanySettings extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8846516289L;

    @Column(name = "settings_id", nullable = false)
    Long settingsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id", updatable = false, insertable = false)
    GenericSettings settings;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "values", columnDefinition = "jsonb")
    Map<GenericSettingsFieldType, Object> values = new LinkedHashMap<>();

    @Column(name = "enabled", columnDefinition = "boolean default false")
    boolean enabled = false;

    public void addValue(String fieldType, Object value) {
        this.values.put(GenericSettingsFieldType.valueOf(fieldType), value);
    }
}
