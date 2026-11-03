package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "rent_act")
@Entity
@SQLDelete(sql = "UPDATE rent_act SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class RentAct extends TradeInActBase {

    @Serial
    private static final long serialVersionUID = 456458258L;

    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", insertable = false, updatable = false)
    private RentOffer offer;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Override
    public String getNumber() {
        return PrefixEnum.AR + "/" + this.getId();
    }

    @OneToMany(mappedBy = "act", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentActAccessory> accessories = new HashSet<>();

    public void addAccessory(RentActAccessory accessory) {
        accessory.setAct(this);
        accessories.add(accessory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentAct rentAct = (RentAct) o;

        return getId() != null ? getId().equals(rentAct.getId()) : rentAct.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
