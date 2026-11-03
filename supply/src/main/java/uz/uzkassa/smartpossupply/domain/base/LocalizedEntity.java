package uz.uzkassa.smartpossupply.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serial;


@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class LocalizedEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3L;

    @Column(name = "name_uz")
    protected String nameUz;

    @Column(name = "name_cyrillic")
    protected String nameCyrillic;

    @Column(name = "name_ru")
    protected String nameRu;

    @Override
    public String getName() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz -> nameUz;
            case cyrillic -> nameCyrillic;
            default -> nameRu;
        };
    }
}
