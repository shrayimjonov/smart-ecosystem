package uz.uzkassa.horeca.dto.eimzo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;

@Getter
@Setter
@ToString
public class SignerDto implements Serializable {

    private TimeStampInfoDto timeStampInfo;
    private LinkedList<CertificateDto> certificate;
}
