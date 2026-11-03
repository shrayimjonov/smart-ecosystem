package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.PaymentType;
import uz.uzkassa.horeca.enums.terminal.TerminalApplicationStatus;
import uz.uzkassa.horeca.enums.terminal.TerminalBusinessType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TerminalCompanyDTO extends TerminalBaseAuditDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 43534573L;

    TerminalFullNameDTO contactFullName;
    String phone;
    Long ctoId;
    String inn;
    String name;
    String brand;
    String oked;
    String accountNumber;
    String mfo;
    String bankId;
    String bankName;
    TerminalApplicationStatus applicationStatus;
    TerminalBusinessType businessType;
    String address;
    TerminalRegionDTO region;
    TerminalCityDTO city;
    List<TerminalActivityTypeDTO> types;
    boolean paysNds;
    BigDecimal ndsPercent;
    boolean warehouseEnabled;
    boolean autoPrintEnabled;
    boolean fiscal;
    LocalDateTime syncDate;
    Set<PaymentType> paymentTypes = new HashSet<>();
    String smsCode;
    String trueToken;
    LocalDateTime tokenDate;
    String apayId;
    String cadastreFileId;
    String applicationFileId;
    boolean commitent;
    Map<String, Integer> balances;
    Map<String, LocalDateTime> permissions;
    boolean freeTrial;
    LocalDateTime trialDate;
    String logoUrl;
    String managerLogin;
    String managerFirstName;
    String managerLastName;
    String managerPatronymic;
    Long ownerId;
    String apayTariffId;

}
