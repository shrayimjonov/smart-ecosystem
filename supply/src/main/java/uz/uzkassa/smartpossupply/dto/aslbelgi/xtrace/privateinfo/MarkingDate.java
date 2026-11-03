package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkingDate implements Serializable {
    LocalDateTime emissionDate;
    LocalDateTime issueDate;
}
