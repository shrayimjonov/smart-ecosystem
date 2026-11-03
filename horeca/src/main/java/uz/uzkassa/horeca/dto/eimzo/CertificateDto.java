package uz.uzkassa.horeca.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateDto implements Serializable {

    private String serialNumber;
    private SubjectDto subjectInfo;
}
