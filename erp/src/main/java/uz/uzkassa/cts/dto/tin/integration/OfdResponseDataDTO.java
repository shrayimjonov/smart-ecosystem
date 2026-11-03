package uz.uzkassa.cts.dto.tin.integration;


import lombok.ToString;

@ToString
public class OfdResponseDataDTO<T> extends OfdResponseDTO {

    private T data;

    public OfdResponseDataDTO() {
    }

    public OfdResponseDataDTO(OfdResponseDTO dto) {
        this.setSuccess(dto.isSuccess());
        this.setText(dto.getText());
        this.setCode(dto.getCode());
    }

    public OfdResponseDataDTO(T data) {
        this.data = data;
    }

    public OfdResponseDataDTO(boolean success, Integer code, String text) {
        super(success, code, text);
    }

    public OfdResponseDataDTO(boolean success, Integer code, String text, T data) {
        super(success, code, text);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
