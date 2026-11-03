package uz.uzkassa.horeca.dto.tasnif;

import java.io.Serial;
import java.util.List;

public class TasnifResponseListDataDTO<T> extends TasnifResponseDTO {

    @Serial
    private static final long serialVersionUID = 233454322L;

    private List<T> data;

    public TasnifResponseListDataDTO() {}

    public TasnifResponseListDataDTO(TasnifResponseDTO dto) {
        this.setSuccess(dto.isSuccess());
        this.setCode(dto.getCode());
        this.setReason(dto.getReason());
    }

    public TasnifResponseListDataDTO(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
