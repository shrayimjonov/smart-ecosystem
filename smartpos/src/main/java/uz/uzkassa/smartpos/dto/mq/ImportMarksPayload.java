package uz.uzkassa.smartpos.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportMarksPayload implements Serializable {
    private Long companyId;
    private String trueToken;
    private String facturaId;
    private String sign;
}
