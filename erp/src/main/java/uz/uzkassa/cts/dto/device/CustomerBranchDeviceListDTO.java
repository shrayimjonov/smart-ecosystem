package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.bill.BillResponseDTO;
import uz.uzkassa.cts.enums.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDeviceListDTO implements Serializable {

    Long id;

    Long deviceId;
    String deviceSerial;

    Long fiscalModuleId;
    String fiscalModuleSerial;

    CustomerBranchOfdDTO customerBranch;

    int employeeTotal;

    String applicationFileUrl;

    Long applicationFileId;

    String ofdFileUrl;

    OfdStatus ofdStatus;

    Long ofdId;

    ApplicationType ofdApplicationType;

    Long fiscalizationId;

    boolean paid;

    Long customerId;

    boolean sync;

    boolean isTransferable;

    LocalDateTime fiscalizationDate;

    DeviceStatus deviceStatus;

    DevicePaymentType paymentType;

    Set<PaymentType> paymentStatuses = new HashSet<>();

    BillResponseDTO bill;

    String fiscalModuleFileUrl;

    Long fiscalModuleFileId;

    DeviceType deviceType;

    Long customerBranchDeviceId;

    Long tariffId;
}
