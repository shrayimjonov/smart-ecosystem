package uz.uzkassa.smartpos.integration.supply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SupplierContractLookupDTO {
    private Long id;
    private LocalDate contractDate;
    private String contractNumber;

}
