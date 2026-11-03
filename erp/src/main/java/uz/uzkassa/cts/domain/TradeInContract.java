package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;

@Entity
@Table(name = "trade_in_contract")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInContract extends TradeInContractBase {

    @Serial
    private static final long serialVersionUID = 5765365078L;

    @Column(name = "trade_in_price_history_id")
    private Long tradeInPriceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_price_history_id", insertable = false, updatable = false)
    private TradeInPriceHistory tradeInPriceHistory;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInAct tradeInAct;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInInvoice tradeInInvoice;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInCounterContract tradeInCounterContract;

    @Override
    public String getNumber() {
        return PrefixEnum.CTI + "/" + this.getId();
    }

    @Column(name = "branch_id")
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    public ContractShortDTO toContractShortDTO() {
        return new ContractShortDTO(this.getId(), this.getNumber(), this.getCreatedDate());
    }
}
