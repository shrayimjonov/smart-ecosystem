package uz.uzkassa.horeca.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractLookUpDTO implements Serializable {

    Long id;

    LocalDate contractDate;

    String contractNumber;
}
