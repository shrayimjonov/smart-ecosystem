package uz.uzkassa.horeca.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serial;
import java.io.Serializable;


@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class LocalizedEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    @Column(name = "name_uz")
    String nameUz;

    @Column(name = "name_cyrillic")
    String nameCyrillic;

    @Column(name = "name_ru")
    String nameRu;

    public String getName() {
        return switch (HeaderUtils.getCurrentRequestLanguageEnum()) {
            case uz -> nameUz;
            case cyrillic -> nameCyrillic;
            default -> nameRu;
        };
    }
}
