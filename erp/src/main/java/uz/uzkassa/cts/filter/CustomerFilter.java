package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerFilter extends BaseFilter {

    Long activityTypeId;

    Long parentActivityTypeId;

    String businessType;

    String ofdStatus;

    Long bankCategoryId;

    Long creatorId;

    Boolean allowDeviceRegister;

    String aPayRegisterStatus;

    String appType;

    String legalStatus;
    Long bankTariffId;

    List<Long> regionIds;
    List<Long> branchIds;
}
