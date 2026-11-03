package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseDTO implements Serializable {
    private String reason;
    private String data;
    private String timeStampTokenB64;
    private boolean success;

    @JsonSetter("timeStampTokenB64")
    public void setTimeStampTokenB64(String timeStampTokenB64) {
        this.data = timeStampTokenB64;
    }
}
