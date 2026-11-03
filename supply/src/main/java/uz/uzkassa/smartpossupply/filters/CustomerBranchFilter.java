package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 21.06.2023
 */
@Getter
@Setter
public class CustomerBranchFilter extends BaseFilter {

    List<Long> productIds;

    Boolean validateCode;

    LocalDateTime lastSyncDate;
}
