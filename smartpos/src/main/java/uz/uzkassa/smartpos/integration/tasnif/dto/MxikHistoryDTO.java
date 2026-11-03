package uz.uzkassa.smartpos.integration.tasnif.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MxikHistoryDTO {
    private String mxik;
    private String newMxikCOde;
    private String status;
    private String name;
    private Object description;
    private int label;
    private String internationalCode;
    private int usePackage;
    private Object createdAt;
    private Object updateAt;
    private List<MxikPackage> packages;
    private int cnt;
}
