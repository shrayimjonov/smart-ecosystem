package uz.uzkassa.horeca.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.enums.OrderReason;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4574873L;

    String status;

    String reason;

    String otherReason;

    boolean withOrder = true;

    Long numberOfGuest;

    String guestName;

    String guestPhone;

    public UpdateStatusDTO(String status) {
        this.status = status;
    }

    public UpdateStatusDTO(String status, Long numberOfGuest) {
        this.status = status;
        this.numberOfGuest = numberOfGuest;
    }

    public UpdateStatusDTO(String status, String reason, String otherReason) {
        this.status = status;
        this.reason = reason;
        this.otherReason = otherReason;
    }

    public UpdateStatusDTO(String status, boolean withOrder) {
        this.status = status;
        this.withOrder = withOrder;
    }

    public UpdateStatusDTO(String status, boolean withOrder, Long numberOfGuest) {
        this.status = status;
        this.withOrder = withOrder;
        this.numberOfGuest = numberOfGuest;
    }
}
