package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "trade_in_counter_contract_file")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_counter_contract_file SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInCounterContractFile extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4494409317L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private TradeInCounterContract contract;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;
}
