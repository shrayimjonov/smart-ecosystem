package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDTO implements Serializable {

    String id;

    Long customerId;

    SelectItem customer;

    @NotNull
    String number;

    Status status;

    String statusName;

    @NotNull
    LocalDate date;

    String note;

    String organizationId;
}
