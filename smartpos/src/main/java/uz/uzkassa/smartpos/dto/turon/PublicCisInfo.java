package uz.uzkassa.smartpos.dto.turon;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PublicCisInfo {
    private String requestedCis;
    private String cis;
    private String status;
    private String packageType;
    private int productGroupId;
    private String productGroup;
    private String gtin;
    private String parent;
    private Set<String> child;
    private String ownerInn;
    private String ownerName;
    private String errorCode;
    private String errorMessage;
}
