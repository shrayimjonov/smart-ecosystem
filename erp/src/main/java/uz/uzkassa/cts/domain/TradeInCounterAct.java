package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;

@Entity
@Table(name = "trade_in_counter_act")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_counter_act SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInCounterAct extends TradeInActBase {

    @Serial
    private static final long serialVersionUID = 4753916645L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private TradeInCounterContract contract;

    @Column(name = "device_serial_number", unique = true)
    private String deviceSerialNumber;

    @Override
    public String getNumber() {
        return PrefixEnum.CATI + "/" + this.getId();
    }
}
