package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MxikPackagesDTO  implements Serializable {
    private String mxikCode;
    private List<MxikInfo> info;
}
