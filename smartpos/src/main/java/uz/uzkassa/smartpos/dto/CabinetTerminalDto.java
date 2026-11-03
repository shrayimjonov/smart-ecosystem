package uz.uzkassa.smartpos.dto;

import uz.uzkassa.smartpos.dto.base.CommonDTO;

public record CabinetTerminalDto(Long id, String serialNumber, CommonDTO branch) {
}
