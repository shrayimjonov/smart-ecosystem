package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.enums.OfdStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchListDTO extends TotalDTO {

    boolean sync;

    boolean reSync;

    Long id;

    String name;

    String brand;

    String phone;

    BaseAddressDetailDTO address;

    List<UploadFileResponse> files;

    CustomerBranchStatus status;

    OfdStatus ofdStatus;

    Long ofdId;

    String cadastreNumber;

    CommonDTO customer;

    String tin;

    boolean hasAct;

    boolean hasInvoicePayment;

    CommonDTO apayCategory;

    CommonDTO activityType;

    CommonDTO parentActivityType;

    String deviceType;
}
