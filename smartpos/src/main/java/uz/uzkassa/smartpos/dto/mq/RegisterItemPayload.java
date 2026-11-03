package uz.uzkassa.smartpos.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterItemPayload implements Serializable {
    private Long branchId;
    private Long productId;
    private String mxik;
    private Long categoryId;
    private String productName;
    private Long unitId;
    private boolean hasMark;
}
