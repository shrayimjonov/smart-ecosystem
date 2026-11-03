package uz.uzkassa.smartpossupply.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.01.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserSupervisorDTO {

    @NotNull
    Long supervisorId;

    @NotNull
    @NotEmpty
    List<Long> managerIds;
}
