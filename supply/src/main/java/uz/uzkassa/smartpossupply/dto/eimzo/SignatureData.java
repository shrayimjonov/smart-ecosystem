package uz.uzkassa.smartpossupply.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureData implements Serializable {
    private String number;
    private String owner;
    private LocalDateTime dateTime;
}
