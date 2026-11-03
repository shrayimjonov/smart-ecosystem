package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class MxikInfo implements Serializable {
    private List<PackageDTO> packageNames;
    private String mxikCode;
    private String nameRu;
}
