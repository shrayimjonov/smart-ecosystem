package uz.uzkassa.horeca.dto.billing.agreement;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAgreementRequestDTO {

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
