package uz.uzkassa.smartpos.domain.postgres.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.dto.factura.BankDTO;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bank")
@Getter
@Setter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class Bank implements Serializable {
    @Id
    private String id;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz")
    private String nameUz;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return Objects.equals(id, bank.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getName() {
        return StringUtils.isNoneBlank(nameRu) ? nameRu : nameUz;
    }

    public BankDTO toDto() {
        return new BankDTO(getId(), getName());
    }
}
