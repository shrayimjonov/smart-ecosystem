package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.02.2023 12:48
 */
@Getter
@Setter
public class TransactionFilter extends BaseFilter {

    EntityTypeEnum entityType;

    Long accountId;
}
