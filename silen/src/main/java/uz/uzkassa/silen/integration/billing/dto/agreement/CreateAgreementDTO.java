package uz.uzkassa.silen.integration.billing.dto.agreement;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.billing.domain.Agreement} entity.
 */
@Getter
@Setter
public class CreateAgreementDTO implements Serializable {
    @NotNull
    private String inn;
    private Long xizmatId;
    private Long instances;
    private String description;
    private String noteFromExternalSystem;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String login;
    private String smartposCreatorId;
    private String promoCode;
    @Override
    public String toString() {
        return "CreateAgreementDTO{" +
                ", inn='" + inn + '\'' +
                ", xizmatId=" + xizmatId +
                ", instances=" + instances +
                ", description='" + description + '\'' +
                ", noteFromExternalSystem='" + noteFromExternalSystem + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", login='" + login + '\'' +
                ", smartposCreatorId='" + smartposCreatorId + '\'' +
                ", promoCode='" + promoCode + '\'' +
                '}';
    }
}
