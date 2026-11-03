package uz.uzkassa.horeca.dto.terminal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class TerminalBaseAuditDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3453453L;

    Long id;

    @JsonProperty(access = READ_ONLY, index = -100)
    String createdBy;

    @JsonProperty(access = READ_ONLY, index = -101)
    LocalDateTime createdDate;

    @JsonProperty(access = READ_ONLY, index = -102)
    String lastModifiedBy;

    @JsonProperty(access = READ_ONLY, index = -103)
    LocalDateTime lastModifiedDate;
}
