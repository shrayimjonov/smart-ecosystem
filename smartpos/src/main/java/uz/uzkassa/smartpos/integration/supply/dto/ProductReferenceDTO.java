package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;
import uz.uzkassa.smartpos.integration.enums.FieldPosition;
import uz.uzkassa.smartpos.integration.enums.ValueType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.08.2023
 */

@Getter
@Setter
@NoArgsConstructor
public class ProductReferenceDTO {

    ClickhouseCommonCodeDTO reference;

    ClickhouseCommonDTO parentReference;

    ValueType valueType;

    FieldPosition position;
}
