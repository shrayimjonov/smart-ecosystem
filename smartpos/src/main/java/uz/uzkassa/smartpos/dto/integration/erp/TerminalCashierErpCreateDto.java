package uz.uzkassa.smartpos.dto.integration.erp;

import jakarta.validation.constraints.NotBlank;

public record TerminalCashierErpCreateDto(
        @NotBlank String login,
        @NotBlank  String firstName,
        @NotBlank   String lastName,
        String patronymic,
        @NotBlank Long branchErpId,
        @NotBlank Long companyErpId
) {
}
