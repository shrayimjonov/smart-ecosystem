package uz.uzkassa.cts.dto.kapitalbank.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InfoRequestDto implements Serializable {

    public InfoRequestDto(String pan, String expiry) {
        this.pan = pan;
        this.expiry = expiry;
    }

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 16, max = 16)
    private String pan;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 4, max = 4)
    private String expiry;

    @Override
    public String toString() {
        return "InfoRequestDto{" +
                "pan='" + pan + '\'' +
                ", expiry='" + expiry + '\'' +
                '}';
    }
}
