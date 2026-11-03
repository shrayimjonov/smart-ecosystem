package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class TelegramAccountFilter extends BaseFilter implements Serializable {
    private Long companyId;
    private Long branchId;
    private Long userId;
    private Boolean confirmed;
    private Boolean enabled;
}
