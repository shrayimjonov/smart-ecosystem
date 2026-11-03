package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.09.2023
 */
@Getter
@Setter
@NoArgsConstructor
public class ClickhouseCommonCodeDTO extends ClickhouseCommonDTO {

    String code;

}
