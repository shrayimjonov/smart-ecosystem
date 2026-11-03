package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "trade_in_act")
@Entity
@SQLDelete(sql = "UPDATE trade_in_act SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInAct extends TradeInActBase {

    @Serial
    private static final long serialVersionUID = 9618190261L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private TradeInContract contract;

    @OneToOne(mappedBy = "tradeInAct", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInInvoice tradeInInvoice;

    @NotNull
    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    private Device device;

    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @Override
    public String getNumber() {
        return PrefixEnum.ATI + "/" + this.getId();
    }


}
