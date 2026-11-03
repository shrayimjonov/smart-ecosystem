package uz.uzkassa.smartpos.integration.meningUyim;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MeningUyimResponse implements Serializable {
    private String message;
    private int code;
    private Object data;
}
