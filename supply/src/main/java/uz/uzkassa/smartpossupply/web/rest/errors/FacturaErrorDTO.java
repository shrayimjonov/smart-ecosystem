package uz.uzkassa.smartpossupply.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class FacturaErrorDTO implements Serializable {

    @JsonProperty("ErrorId")
    String errorId;

    @JsonProperty("NameRu")
    String nameRu;

    @JsonProperty("NameUz")
    String nameUz;

    @JsonProperty("ErrorCode")
    Integer errorCode;

    @JsonProperty("ErrorMessage")
    String errorMessage;

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public void setNameUz(String nameUz) {
        this.nameUz = nameUz;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonSetter("errorId")
    public void seterrorid(String errorid) {
        this.errorId = errorid;
    }

    @JsonSetter("nameru")
    public void setnameru(String nameru) {
        this.nameRu = nameru;
    }

    @JsonSetter("nameuz")
    public void setnameuz(String nameuz) {
        this.nameUz = nameuz;
    }

    @JsonSetter("errorcode")
    public void seterrorcode(Integer errorcode) {
        this.errorCode = errorcode;
    }

    @JsonSetter("errormessage")
    public void seterrormessage(String errormessage) {
        this.errorMessage = errormessage;
    }
}
