package uz.uzkassa.cts.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.enums.PrefixEnum;

import jakarta.persistence.*;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trade_in_counter_contract")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_counter_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInCounterContract extends TradeInContractBase {

    @Serial
    private static final long serialVersionUID = 7368420922L;

    @Column(name = "trade_in_counter_device_price_id")
    private Long tradeInCounterDevicePriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_counter_device_price_id", insertable = false, updatable = false)
    private TradeInCounterDevicePrice tradeInCounterDevicePrice;

    @Column(name = "trade_in_device_id")
    private Long tradeInDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_device_id", insertable = false, updatable = false)
    private TradeInDevice tradeInDevice;

    @Column(name = "device_serial_number", unique = true)
    private String deviceSerialNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private TradeInContract contract;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private TradeInCounterAct tradeInCounterAct;

    @Column(name = "note_file_uri")
    private String noteFileUri;

    @Column(name = "note_file_name")
    private String noteFileName;

    @Column(name = "note_content_type")
    private String noteContentType;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TradeInCounterContractFile> files = new HashSet<>();

    public void addFile(TradeInCounterContractFile file) {
        file.setContract(this);
        files.add(file);
    }

    @Override
    public String getNumber() {
        return PrefixEnum.CCTI + "/" + this.getId();
    }
}
