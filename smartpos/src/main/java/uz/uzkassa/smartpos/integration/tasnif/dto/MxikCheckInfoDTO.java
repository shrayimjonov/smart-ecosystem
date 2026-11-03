package uz.uzkassa.smartpos.integration.tasnif.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MxikCheckInfoDTO {
    private String mxikCode;
    private String internationalCode;
    private String subPositionName;
    private Object brandName;
    private String name;
    private Object attributName;
    private String nameUzCyrl;
    private String nameUzLatin;
    private String nameRu;
    private Object unitCode;
    private Object unitName;
    private Object commonUnitCode;
    private Object commonUnitName;
    private int label;
    private int labelTNN;
    private String usePackage;
    private String useLicense;
    private String isAllowedForOneSidedFactura;
    private Object units;
    private List<MxikPackage> packageNames;
}
