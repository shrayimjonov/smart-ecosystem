package uz.uzkassa.smartpossupply.dto.mailinglist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    @NotNull
    String description;

    @NotNull
    Long companyId;

    @NotEmpty
    @NotNull
    List<Long> customers;
}
