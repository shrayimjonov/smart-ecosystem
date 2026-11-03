package uz.uzkassa.horeca.dto.eimzo;

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
public class Pkcs7InfoDto implements Serializable {

    private LinkedList<SignerDto> signers;
    private String documentBase64;
}
