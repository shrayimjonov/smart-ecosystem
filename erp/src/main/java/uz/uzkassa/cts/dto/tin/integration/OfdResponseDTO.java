package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OfdResponseDTO {

    private boolean success;
    private Integer code;
    private String text;
    private List<OfdErrorDTO> errors;

    public OfdResponseDTO() {
    }

    public OfdResponseDTO(boolean success, Integer code, String text) {
        this.success = success;
        this.code = code;
        this.text = text;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<OfdErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<OfdErrorDTO> errors) {
        this.errors = errors;
    }
}
