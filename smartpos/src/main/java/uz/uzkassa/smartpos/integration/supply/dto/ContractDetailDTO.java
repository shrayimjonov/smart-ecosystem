package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.enums.ContractStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ContractDetailDTO implements Serializable {

    Long id;

    CommonDTO customer;

    LocalDate contractDate;

    String contractNumber;

    String description;

    ContractStatus status;
}
