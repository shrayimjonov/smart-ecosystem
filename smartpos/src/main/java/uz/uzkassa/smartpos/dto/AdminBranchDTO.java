package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ApayRegisterStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBranchDTO implements Serializable {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String companyName;
    private Long companyId;
    private Long terminalCount;
    private Long userCount;
    private String serialNumber;
    private List<String> terminalSerialNumbers;
    private LocalDateTime createdDate;
    private LocalDateTime cashBoxCreatedDate;
    private ApayRegisterStatus apayStatus;
}
