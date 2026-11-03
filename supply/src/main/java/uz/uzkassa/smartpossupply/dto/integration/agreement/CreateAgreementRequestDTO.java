package uz.uzkassa.smartpossupply.dto.integration.agreement;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String tinForInstanceCheck;
    private String idForInstanceCheck;
    private String nameForInstanceCheck;
    private Long parentAgreementId;
    private List<String> serialNumbers;

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
