package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilisationDTO implements Serializable {

    @Size(min = 1, max = 30_000)
    Set<String> cises;

    String organizationId;

    String serialNumber;

    LocalDateTime productionDate = LocalDateTime.now();

    LocalDateTime expirationDate;
}
