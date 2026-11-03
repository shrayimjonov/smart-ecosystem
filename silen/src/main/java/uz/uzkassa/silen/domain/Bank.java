package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.invoice.BankDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;


@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "bank")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class Bank implements Serializable {
    @Id
    private String id;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz")
    private String nameUz;

    public String getName(){
        final String locale = SecurityUtils.getCurrentLocale();
        String name = switch (locale) {
            case "uz":
                yield this.getNameUz();
            default:
                yield this.getNameRu();
        };
        if (name == null || name.trim().isEmpty()) {
            name = this.getNameRu();
        }
        return name;
    }

    public BankDTO toDto(){
        return new BankDTO(getId(), getName());
    }
}
