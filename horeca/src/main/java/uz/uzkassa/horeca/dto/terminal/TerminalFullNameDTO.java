package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminalFullNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 22995656L;

//    @NotBlank
    @Size(max = 50)
    String firstName;

//    @NotBlank
    @Size(max = 50)
    String lastName;

    @Size(max = 50)
    String patronymic;

    String phone;

    public String getName() {
        return lastName + " " + firstName;
    }

    public TerminalFullNameDTO(String firstName, String lastName, String patronymic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
    }
}
