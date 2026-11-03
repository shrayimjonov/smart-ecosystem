package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.*;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HumoConnectFilter extends BaseFilter {

    HumoConnectOperatorStatus operatorStatus;
    List<Long> caseCreatorIds;
    CustomerAppType appType;
    Boolean license;
    List<CaseType> caseTypes;
    DocFlowStatus docFlowStatus;
    BankType bankType;
    PaymentType paymentType;
    Long caseId;
    List<HumoConnectStatus> statuses;
}
