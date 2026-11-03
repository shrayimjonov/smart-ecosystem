package uz.uzkassa.smartpos.dto.integration.agreement;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateAgreementDTO implements Serializable {

    @NotNull
    private String inn;
    @NotNull
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
