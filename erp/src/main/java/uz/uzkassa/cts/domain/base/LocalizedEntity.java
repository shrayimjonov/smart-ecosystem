package uz.uzkassa.cts.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;


@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public class LocalizedEntity extends BaseEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 334543L;

    @Column(name = "name_uz_latin", length = 2000)
    String nameUzLatin;

    @Column(name = "name_uz_cyrillic", length = 2000)
    String nameUzCyrillic;

    @Column(name = "name_ru", length = 2000)
    String nameRu;

    public String getName() {
        return switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz_latin -> nameUzLatin;
            case uz_cyrillic -> nameUzCyrillic;
            default -> nameRu;
        };
    }

    public String getNameNotNullable() {
        return Optional.ofNullable(getName()).orElse(nameUzCyrillic);
    }

    public void setNameUzLatin(String nameUzLatin) {
        addChangeLog("nameUzLatin", this.nameUzLatin, nameUzLatin);
        this.nameUzLatin = nameUzLatin;
    }

    public void setNameUzCyrillic(String nameUzCyrillic) {
        addChangeLog("nameUzCyrillic", this.nameUzCyrillic, nameUzCyrillic);
        this.nameUzCyrillic = nameUzCyrillic;
    }

    public void setNameRu(String nameRu) {
        addChangeLog("nameRu", this.nameRu, nameRu);
        this.nameRu = nameRu;
    }

    @Override
    public Long getEntityId() {
        return null;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }
}
