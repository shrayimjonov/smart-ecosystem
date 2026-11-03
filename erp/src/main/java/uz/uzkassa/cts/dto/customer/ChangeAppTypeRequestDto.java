package uz.uzkassa.cts.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAppTypeRequestDto {
    private String tin;
    private String appType;
    private Long xizmatId;
    private String xizmatName;
}
