package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchOfdDTO extends CommonDTO {

    Long ofdId;
    String scannerFileUri;
    String scannerFileName;
    Integer scannerCode;
    String scannerName;

    public CustomerBranchOfdDTO(Long id, String name, Long ofdId) {
        super(id, name);
        this.ofdId = ofdId;
    }
}
