package uz.uzkassa.smartpos.integration.tasnif.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MxikGetInformationDto {
    private String mxik;
    private String name;
    private String internationCode;
    private List<MxikPackage> packages;
}
