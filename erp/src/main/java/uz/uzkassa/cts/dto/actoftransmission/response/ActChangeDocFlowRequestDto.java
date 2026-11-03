package uz.uzkassa.cts.dto.actoftransmission.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.uzkassa.cts.enums.ActDocFlowStatus;

import java.io.Serializable;

@Data
public class ActChangeDocFlowRequestDto implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    private ActDocFlowStatus docFlowStatus;
    private String docFlowReason;
}
