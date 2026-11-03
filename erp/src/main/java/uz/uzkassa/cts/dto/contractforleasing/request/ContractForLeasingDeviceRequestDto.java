package uz.uzkassa.cts.dto.contractforleasing.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContractForLeasingDeviceRequestDto {
    private List<Long> deviceIds = new ArrayList<>();
}
