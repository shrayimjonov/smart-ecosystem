package uz.uzkassa.smartpos.integration.apay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.enums.ApplicationStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ApplicationStatusDTO implements Serializable {
    @NotBlank
    String id;
    @NotNull
    ApplicationStatus status;
    @SerializedName(value = "terminalId")
    String fiscalNumber;
}
