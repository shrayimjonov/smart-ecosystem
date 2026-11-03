package uz.uzkassa.silen.integration.asilbelgi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisInfoResponse implements Serializable {
    private CisInfo cisInfo;
    private String errorCode;
    private String errorMessage;
}
