package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.CustomerBranchScanner;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.customerBranchScanner.CustomerBranchScannerDTO;
import uz.uzkassa.cts.filter.CBScannerFilter;

import java.util.List;

public interface CustomerBranchScannerService extends BaseService<CustomerBranchScanner, Long> {

    Long create(CustomerBranchScannerDTO scannerDTO);

    Long update(Long id, CustomerBranchScannerDTO scannerDTO);

    Page<CommonCodeDTO> list(CBScannerFilter filter);

    List<CommonCodeDTO> lookUp(CBScannerFilter filter);

    CommonCodeDTO get(Long id);

    void sync();
}
