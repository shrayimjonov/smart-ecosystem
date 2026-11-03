package uz.uzkassa.horeca.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.LocalizedEntity;
import uz.uzkassa.horeca.dto.bank.BankLookUpDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bank",
        indexes = {
                @Index(columnList = "mfo", name = "bank_mfo_idx", unique = true),
                @Index(columnList = "name_uz", name = "bank_name_uz_idx"),
                @Index(columnList = "name_cyrillic", name = "bank_name_cyrillic_idx"),
                @Index(columnList = "name_ru", name = "bank_name_ru_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@SQLDelete(sql = "UPDATE bank set deleted='false' WHERE id=?")
public class Bank extends LocalizedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7L;

    @NotNull
    @Column(name = "mfo", nullable = false, unique = true)
    String mfo;

    public BankLookUpDTO toLookUpDTO() {
        return new BankLookUpDTO(getId(), getName(), getMfo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bank)) {
            return false;
        }
        return id != null && id.equals(((Bank) o).id);
    }
}
