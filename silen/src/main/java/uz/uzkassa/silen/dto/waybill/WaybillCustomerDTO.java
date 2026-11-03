package uz.uzkassa.silen.dto.waybill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WaybillCustomerDTO implements Serializable {

    Long id;

    String tin;

    String name;

    boolean isCommittent;

}
