package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class TINBranchDTO implements Serializable {
    private String tin;
    private String name;
    private String branchName;
    private String branchNum;

    public String getName() {
        return StringUtils.isNotEmpty(this.branchName) ? branchName : name;
    }
}
