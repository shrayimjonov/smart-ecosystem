package uz.uzkassa.smartpos.integration.tasnif.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TasnifResponseDTO<T> {
    private boolean success;
    private Object code;
    private String reason;
    private T data;
    private Object errors;
    private int recordTotal;
}
