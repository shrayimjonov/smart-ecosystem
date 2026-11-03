package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "ledger_test")
@Getter
@Setter
public class LedgerTest extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 493445724L;

    @Column(name = "count")
    private Integer count;

    @Column(name = "a_pay_count")
    private Integer aPayCount;

    @Column(name = "tin")
    private String tin;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 1500)
    private String description;
}
