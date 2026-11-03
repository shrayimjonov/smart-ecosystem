package uz.uzkassa.silen.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pkcs7Dto implements Serializable {
    private String pkcs7b64;
    private Pkcs7InfoDto pkcs7Info;
    private List<SignatureCertificateDto> timestampedSignerList;
    private SignatureCertificateDto subjectCertificateInfo;
    private int status;
    private String message;

    public boolean isSuccess() {
        return this.status == 1;
    }
}
