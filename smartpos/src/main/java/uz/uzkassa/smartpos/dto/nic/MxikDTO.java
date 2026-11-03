package uz.uzkassa.smartpos.dto.nic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.Mxik;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MxikDTO implements Serializable {
    private String mxik;
    private String name;
    private String internalCode;
    private List<MxikPackage> packages;
    private boolean hasMark;

    public MxikDTO(Mxik mxik) {
        this.mxik = mxik.getMxik();
        this.name = mxik.getName();
        this.internalCode = mxik.getBarcode();
        this.packages = mxik.getPackages();
        this.hasMark = mxik.getLabel() != null && mxik.getLabel() > 0;
    }
}
