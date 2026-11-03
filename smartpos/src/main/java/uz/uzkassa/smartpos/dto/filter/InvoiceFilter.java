package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class InvoiceFilter extends BaseFilter implements Serializable {
    protected Status status;
    protected String sellerTin;
    protected String buyerTin;
}
