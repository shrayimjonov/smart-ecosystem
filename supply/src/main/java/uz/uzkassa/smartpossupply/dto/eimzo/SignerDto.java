package uz.uzkassa.smartpossupply.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignerDto implements Serializable {
    private TimeStampInfoDto timeStampInfo;
    private LinkedList<CertificateDto> certificate;
}
