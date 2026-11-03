package uz.uzkassa.silen.dto.eimzo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class SignatureData implements Serializable {
    private String number;
    private String owner;
    private LocalDateTime dateTime;
}
