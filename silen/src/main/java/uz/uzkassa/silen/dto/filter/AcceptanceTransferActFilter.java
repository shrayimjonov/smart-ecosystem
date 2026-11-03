package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ActStatus;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActFilter extends Filter {

    String customerId;

    String customerTin;

    String supplierTin;

    Integer year;

    ActStatus status;
}
