package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "receipt_limit")
public class ReceiptLimit implements Serializable {
    @Id
    private String code;

    @Column(name = "name_uz_latin")
    private String nameUzLatin;

    @Column(name = "name_uz_cyrillic")
    private String nameUzCyrillic;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;

}
