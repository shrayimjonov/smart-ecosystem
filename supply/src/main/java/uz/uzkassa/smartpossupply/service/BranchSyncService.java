package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.sync.company.*;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceBranchSyncDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.integration.ErpClient;
import uz.uzkassa.smartpossupply.integration.MarketplaceClient;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchSyncService extends BaseService {

    MarketplaceClient marketplaceClient;
    ErpClient erpClient;

    @Transactional
    public void syncQueue(SyncPayload payload) {
        if (AppType.MARKETPLACE == payload.getAppType()) {
            this.syncToMarketplace(payload);
        } else if (AppType.ERP == payload.getAppType()) {
            this.sync(payload);
        }
    }

    private void syncToMarketplace(SyncPayload payload) {
        Branch branch = branchRepository.findById(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", payload.getId()));
        MarketplaceBranchSyncDTO marketplaceBranchSyncDTO = new MarketplaceBranchSyncDTO();
        marketplaceBranchSyncDTO.setId(branch.getId());
        marketplaceBranchSyncDTO.setName(branch.getName());
        marketplaceBranchSyncDTO.setCompanyId(branch.getCompanyId());
        marketplaceBranchSyncDTO.setAppType(AppType.SUPPLY.name());
        if (branch.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            addressSyncDTO.setRegionCode(branch.getAddress().getRegion().getCode());
            addressSyncDTO.setDistrictCode(branch.getAddress().getDistrict().getCode());
            addressSyncDTO.setStreet(branch.getAddress().getStreet());
            addressSyncDTO.setHouse(branch.getAddress().getHouse());
            addressSyncDTO.setApartment(branch.getAddress().getApartment());
            addressSyncDTO.setLongitude(branch.getAddress().getLongitude());
            addressSyncDTO.setLatitude(branch.getAddress().getLatitude());
            marketplaceBranchSyncDTO.setAddress(addressSyncDTO);
        }
        branch.setMarketplaceId(marketplaceClient.syncBranch(marketplaceBranchSyncDTO));
        branchRepository.save(branch);
    }

    private void sync(SyncPayload payload) {
        Branch branch = branchRepository.findById(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", payload.getId()));
        CompanyBranchSyncDTO companyBranchSyncDTO = new CompanyBranchSyncDTO();
        companyBranchSyncDTO.setId(branch.getId());
        companyBranchSyncDTO.setName(branch.getName());
        companyBranchSyncDTO.setCompany(branch.getCompany().toCommonBaseDTO());
        companyBranchSyncDTO.setBrand(branch.getName());

        ActivityTypeSyncDTO activityTypeSyncDTO = new ActivityTypeSyncDTO();
        if (branch.getCompany().getActivityType() != null) {
            activityTypeSyncDTO.setCode(branch.getCompany().getActivityType().getCode());
            if (branch.getCompany().getActivityType().getParent() != null) {
                activityTypeSyncDTO.setParentCode(branch.getCompany().getActivityType().getParent().getCode());
            }
        }
        companyBranchSyncDTO.setActivityType(activityTypeSyncDTO);

        if (branch.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            addressSyncDTO.setRegionCode(branch.getAddress().getRegion().getCode());
            addressSyncDTO.setDistrictCode(branch.getAddress().getDistrict().getCode());
            addressSyncDTO.setStreet(branch.getAddress().getStreet());
            addressSyncDTO.setHouse(branch.getAddress().getHouse());
            addressSyncDTO.setApartment(branch.getAddress().getApartment());
            addressSyncDTO.setLongitude(branch.getAddress().getLongitude());
            addressSyncDTO.setLatitude(branch.getAddress().getLatitude());
            companyBranchSyncDTO.setAddress(addressSyncDTO);
        }
        if (AppType.ERP == payload.getAppType()) {
            Long erpId = erpClient.syncBranch(companyBranchSyncDTO);
            if (erpId != null) {
                branch.setErpId(erpId);
                branchRepository.save(branch);
            }
        }
    }
}