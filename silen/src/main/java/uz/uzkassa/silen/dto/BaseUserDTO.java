package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserDTO {

    protected String id;

    @Size(min = 1, max = 15)
    protected String pinfl;

    @Size(min = 1, max = 50)
    protected String login;

    @Size(max = 50)
    protected String firstName;

    @Size(max = 50)
    protected String lastName;
}
