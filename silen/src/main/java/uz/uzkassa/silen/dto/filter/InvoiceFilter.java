package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.InvoiceType;
import uz.uzkassa.silen.enumeration.Status;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceFilter extends Filter {

    Status status;

    InvoiceType type;

    String buyerTin;

    String sellerTin;
}
