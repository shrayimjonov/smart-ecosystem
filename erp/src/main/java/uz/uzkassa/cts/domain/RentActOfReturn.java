package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "rent_act_of_return")
@Entity
@SQLDelete(sql = "UPDATE rent_act_of_return SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class RentActOfReturn extends TradeInActBase {

    @Serial
    private static final long serialVersionUID = 7467743704L;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Override
    public String getNumber() {
        return PrefixEnum.ARR + "/" + this.getId();
    }

    @OneToMany(mappedBy = "actOfReturn", cascade = CascadeType.MERGE)
    private Set<RentActAccessory> accessories = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentActOfReturn rentActOfReturn = (RentActOfReturn) o;

        return getId() != null ? getId().equals(rentActOfReturn.getId()) : rentActOfReturn.getId() == null;
    }

}
