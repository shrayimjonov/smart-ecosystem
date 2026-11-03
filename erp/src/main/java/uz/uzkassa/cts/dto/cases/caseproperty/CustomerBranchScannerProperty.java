package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.CustomerBranchScanner;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.repository.CaseFileRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerBranchScannerRepository;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.CustomerBranchOldService;
import uz.uzkassa.cts.service.CustomerOldService;

import java.util.List;

@Getter
@Setter
@Component
public class CustomerBranchScannerProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CaseService caseService;
    @Autowired
    @JsonIgnore
    private CustomerOldService customerOldService;
    @Autowired
    @JsonIgnore
    private CustomerBranchDeviceRepository customerBranchDeviceRepository;
    @Autowired
    @JsonIgnore
    private CustomerBranchOldService customerBranchOldService;
    @Autowired
    @JsonIgnore
    private CaseFileRepository caseFileRepository;
    @Autowired
    @JsonIgnore
    private CustomerBranchScannerRepository scannerRepository;


    @NotNull
    private Long customerId;
    @NotNull
    private Long customerBranchId;
    @NotNull
    private Long scannerId;
    private Long caseId;
    private String scannerName;
    private String deviceSerialNumber;
    private String fmSerialNumber;

    @Override
    public void accept() {
        CustomerBranchScanner scanner = scannerRepository.findById(scannerId).orElseThrow();
        CustomerBranch customerBranch = customerBranchOldService.findById(customerBranchId);
        for (CaseFile file : caseFileRepository.findByCaseId(caseId)) {
            customerBranch.setScannerFileName(file.getFileName());
            customerBranch.setScannerFileUri(file.getFileUrl());
        }
        customerBranch.setScannerId(scannerId);
        customerBranch.setCustomerBranchScanner(scanner);
        customerBranchOldService.save(customerBranch);
    }

    @Override
    public List<String> generateTemplate() {
        throw new UnsupportedOperationException("Шаблон не предусмотрен");
    }

    @Override
    public boolean is2Step() {
        return false;
    }
}
