package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.01.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyOneCIntegrationDTO {

    Long id;

    @NotBlank
    String url;

    @NotBlank
    String username;

    @NotBlank
    String password;

    Long companyId;

    boolean oneCEnabled = false;
}
