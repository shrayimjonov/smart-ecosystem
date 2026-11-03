package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.LocalizedEntity;
import uz.uzkassa.smartpossupply.dto.bank.BankLookUpDTO;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name = "bank",
    indexes = {
        @Index(columnList = "name_ru", name = "bank_name_ru_idx"),
        @Index(columnList = "name_uz", name = "bank_name_uz_idx"),
        @Index(columnList = "name_cyrillic", name = "bank_name_cyrillic_idx"),
        @Index(columnList = "code", name = "bank_code_idx"),
        @Index(columnList = "mfo", name = "bank_mfo_idx"),
        @Index(columnList = "code, mfo, name_ru, name_cyrillic, name_uz", name = "bank_search_keys_index"),
    }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class Bank extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 7L;

    @Size(min = 2, max = 2)
    @Column(name = "code", length = 2)
    String code;

    @Column(name = "mfo", length = 6)
    String mfo;

    @Column(name = "tin", length = 14)
    String tin;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Bank parent;

    public BankLookUpDTO toLookUpDTO() {
        return new BankLookUpDTO(getId(), getName(), getMfo());
    }

}
