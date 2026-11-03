package uz.uzkassa.cts.dto.tin.integration;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfdFullResponseDataDTO<T> extends OfdFullResponseDTO {

    private List<T> data;

    public OfdFullResponseDataDTO() {
    }

    public OfdFullResponseDataDTO(OfdFullResponseDTO dto) {
        this.setSuccess(dto.isSuccess());
        this.setText(dto.getText());
        this.setCode(dto.getCode());
        this.setErrors(dto.getErrors());
        this.setRecordTotal(dto.getRecordTotal());
    }

    public OfdFullResponseDataDTO(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
