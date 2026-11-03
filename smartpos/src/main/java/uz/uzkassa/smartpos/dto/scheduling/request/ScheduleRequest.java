package uz.uzkassa.smartpos.dto.scheduling.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class ScheduleRequest {
    @Schema(hidden = true)
    private Long id;
    private Long branchId;
    private Integer month;
    private String originUid;
    private boolean forCredit;
    private List<ProductScheduleRequest> products;
}
