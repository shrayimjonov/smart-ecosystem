package uz.uzkassa.horeca.dto.terminal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TerminalReceiptDTO extends TerminalReceiptGeneralDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 234230987L;

    Double lat;
    Double lon;
    @Size(max = 255)
    String customerName;
    @Size(max = 255)
    String customerPhone;
    boolean readOnly;
    boolean forceToPrint;

    String uid;

    LocalDateTime receiptDateTime;

    @JsonIgnore
    TerminalReceiptPayload payload;
    int positionCount;
    String facturaId;
    Long orderId;
    boolean sendSms;
    String language;

    CommonDTO table;

    CommonDTO order;
    OrderStatus orderStatus;
    boolean fromBatch = false;
    Long oldReceiptId;

    String epsQrCode;
}
