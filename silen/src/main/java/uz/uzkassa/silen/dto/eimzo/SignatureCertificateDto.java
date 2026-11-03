package uz.uzkassa.silen.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureCertificateDto implements Serializable {
    private String serialNumber;
    private SubjectDto subjectName;
    private String validFrom;
    private String validTo;
}
