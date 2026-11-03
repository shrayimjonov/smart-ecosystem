package uz.uzkassa.smartpos.dto.markings;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;

@Getter
@Setter
public class MarkingRequestDTO {
    private Long branchId;
    private Long companyId;
    private String ikpu;
    @NotBlank
    private String code;
    boolean forReturn;

    public String getCode() {
        if (StringUtils.isNotBlank(code)) {
            if (StringUtils.isNotBlank(ikpu)) {
                return MarkingUtils.clearWithProductGroup(code, MarkingUtils.getGroupCode(ikpu));
            } else {
                return MarkingUtils.clearWithProductGroup(code, null);
            }
        }
        return code;
    }
}
