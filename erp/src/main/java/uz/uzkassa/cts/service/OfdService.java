package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.customer.CustomerBalanceDTO;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.dto.ofd.CustomerFiscalDTO;
import uz.uzkassa.cts.dto.tin.integration.*;

public interface OfdService {

    String makeCustomerFiscal(CustomerFiscalDTO customerFiscalDTO);

    String sendCustomerBranch(CustomerBranchSendDTO customerBranchSendDTO);

    OfdCheckTinResponseResultDTO checkCustomer(String code);

    OfdRegionListResponseDTO getRegions(String language);

    OfdRegionListResponseDTO getDistricts(Integer regionCode, String language);

    OfdNeighborhoodListResponseDTO getNeighborhoods(Long regionCode, Long districtCode);

    OfdActivityGroupListResponseDTO getActivityTypeGroups();

    OfdActivityGroupListResponseDTO getActivityTypes(Integer groupCode);

    String getTerminalTypes();

    OfdGenerateApplicationFileDTO generateApplicationFile(OfdGenerateApplicationFileRequestDTO fileGenerateRequestDTO);

    CustomerBalanceDTO checkCustomerBalance(String tin);

    //TxkmResponseDTO getTxkm(Integer pageNo, Integer pageSize, String serialNumber, boolean type);
}
