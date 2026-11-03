package uz.uzkassa.smartpossupply.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimestampSignatureDTO implements Serializable {

    String reason;
    String data;
    String timeStampTokenB64;
    boolean success;

    @JsonSetter("timeStampTokenB64")
    public void setTimeStampTokenB64(String timeStampTokenB64) {
        this.data = timeStampTokenB64;
    }
}
