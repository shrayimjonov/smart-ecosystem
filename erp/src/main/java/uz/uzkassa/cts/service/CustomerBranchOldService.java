package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.sync.SyncCustomerBranchListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyCustomerBranchFilter;
import uz.uzkassa.cts.filter.CustomerBranchFilter;

import java.util.List;

public interface CustomerBranchOldService extends BaseService<CustomerBranch, Long> {

    CustomerBranchOldDTO create(CustomerBranchOldDTO customerBranchOldDTO);

    Page<CustomerBranchListDTO> list(CustomerBranchFilter filter);

    Page<AdminCustomerBranchListDTO> getAll(CompanyCustomerBranchFilter filter);

    Page<AdminCustomerBranchListByFiscalDevicesDTO> getAllByFiscalDevices(CompanyCustomerBranchFilter filter);

    List<CustomerBranchListMapDTO> listForMap(CompanyCustomerBranchFilter filter);

    List<CommonDTO> lookupForOfd(CustomerBranchFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    CustomerBranchLookUpDTO getItems(BaseFilter filter);

    CustomerBranchUpdateDTO update(CustomerBranchUpdateDTO customerBranchUpdateDTO);

    CustomerBranchConfirmDocDTO confirmDoc(CustomerBranchConfirmDocDTO customerBranchConfirmDocDTO);

    CustomerBranchDetailDTO get(Long id);

    void activate(Long id, String documentType, MultipartFile file);

    void uploadDocument(Long id, String documentType, MultipartFile file);

    void deleteDocument(Long id, Long fileId);

    Page<ChangeLogListDTO> getHistories(CustomerBranchFilter filter);

    Page<CustomerBranchShortListDTO> listByEmployee(BaseFilter filter);

    Long getListCountToSync();

    Page<SyncCustomerBranchListDTO> getListToSync(BaseFilter filter);

    List<CommonDTO> getEmptyCustomerBranches(Long customerId);

    void syncWithSmartPos(Long id);

    void syncCustomerBranchListAPay();

    Long connectScanner(Long customerBranchId, Long scannerId, MultipartFile file);

    Long removeScanner(Long id);
}
