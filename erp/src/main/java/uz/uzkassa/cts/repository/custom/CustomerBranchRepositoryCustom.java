package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.sync.SyncCustomerBranchListDTO;
import uz.uzkassa.cts.filter.*;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CustomerBranchRepositoryCustom {

    ResultList<CustomerBranch> getResultList(CustomerBranchFilter filter);

    ResultList<CustomerBranch> getList(CustomerBranchFilter filter, Branch branch);

    List<CustomerBranchListMapDTO> listForMap(CompanyCustomerBranchFilter filter);

    ResultList<CustomerBranch> getAllList(CompanyCustomerBranchFilter filter);

    ResultList<AdminCustomerBranchListByFiscalDevicesDTO> getAllByFiscalDevices(CompanyCustomerBranchFilter filter);

    List<CommonDTO> getListForOfd(CustomerBranchFilter filter);

    ResultList<CommonDTO> getLookUp(BaseFilter filter);

    ResultList<CustomerBranchShortListDTO> getListByEmployee(BaseFilter filter);

    List<SyncCustomerBranchListDTO> getListToSync(BaseFilter filter);

    List<CustomerBranchItemsDTO> getCustomerBranchByTinOrSerialNumber(BaseFilter filter);

    List<CustomerBranchResponseAPayDTO> getCustomerBranches(FilterPayload filter);

    BigInteger countCustomerBranches();
}
