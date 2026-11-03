package uz.uzkassa.cts.dto.customerbranch.rent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchRentDTO implements Serializable {

    Long id;

    Long customerBranchId;

    String rentNumber;

    LocalDate startDate;

    LocalDate endDate;
}
