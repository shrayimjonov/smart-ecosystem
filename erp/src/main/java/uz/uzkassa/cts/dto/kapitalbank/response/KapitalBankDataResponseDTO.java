package uz.uzkassa.cts.dto.kapitalbank.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KapitalBankDataResponseDTO<T> extends KapitalBankResponseDTO {


    private T data;

    public KapitalBankDataResponseDTO() {
    }

    public KapitalBankDataResponseDTO(KapitalBankResponseDTO dto) {
        this.setStatus(dto.getStatus());
        this.setTimestamp(dto.getTimestamp());
        this.setErrorMessage(dto.getErrorMessage());
    }

    public KapitalBankDataResponseDTO(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
