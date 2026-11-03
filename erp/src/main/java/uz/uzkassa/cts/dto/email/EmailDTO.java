package uz.uzkassa.cts.dto.email;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EmailDTO implements Serializable {

    String from;

    String to;

    String subject;

    String message;
}
