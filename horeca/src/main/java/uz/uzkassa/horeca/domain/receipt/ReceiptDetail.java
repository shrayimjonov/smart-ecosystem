package uz.uzkassa.horeca.domain.receipt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.dto.terminal.TerminalAdditionalDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalSource;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "receipt_detail")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptDetail extends SimpleEntity implements Serializable {

    @NotNull
    @Column(name = "receipt_id", nullable = false)
    Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", insertable = false, updatable = false)
    Receipt receipt;

    @Column(name = "longitude")
    Double longitude;

    @Column(name = "latitude")
    Double latitude;

    @Column(name = "customer_name")
    String customerName;

    @Column(name = "customer_phone")
    String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    TerminalSource source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    TerminalAdditionalDTO additional;

    @Column(name = "send_sms", columnDefinition = "boolean default false")
    boolean sendSms;
}
