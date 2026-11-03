package uz.uzkassa.smartpos.dto.integration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BranchJson implements Serializable {
    private Long branchId;
    private String branchName;
}
