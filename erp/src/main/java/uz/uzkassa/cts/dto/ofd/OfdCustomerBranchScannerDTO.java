package uz.uzkassa.cts.dto.ofd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfdCustomerBranchScannerDTO implements Serializable {

    List<OfdFileDTO> file;

    Integer type;

    public OfdCustomerBranchScannerDTO() {
    }

}
