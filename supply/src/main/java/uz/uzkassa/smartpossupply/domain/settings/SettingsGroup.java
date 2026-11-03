package uz.uzkassa.smartpossupply.domain.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "settings_group")
public class SettingsGroup extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 748291635L;

    @Column(name = "name_uz")
    String nameUz;

    @Column(name = "name_cyrillic")
    String nameCyrillic;

    @Column(name = "name_ru", nullable = false)
    String nameRu;

    @Column(name = "order_number")
    Integer orderNumber;

    @Override
    public String getName() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz -> nameUz;
            case cyrillic -> nameCyrillic;
            default -> nameRu;
        };
    }
}
