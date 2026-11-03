package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AdminCustomerBranchListDTO implements Serializable {

    boolean sync;

    boolean reSync;

    Long id;

    String name;

    String brand;

    String phone;

    CustomerBranchStatus status;

    OfdStatus ofdStatus;

    Long ofdId;

    String cadastreNumber;

    CustomerBranchRentDTO rent;

    CommonDTO customer;

    CommonDTO branch;

    String tin;

    BaseAddressDetailDTO address;

    boolean hasAct;

    boolean hasInvoicePayment;

    LocalDateTime registrationDate;

    String deviceType;
}
