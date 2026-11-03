package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.sync.company.*;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.*;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.integration.ErpClient;
import uz.uzkassa.smartpossupply.integration.MarketplaceClient;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanySyncService extends BaseService {

    MarketplaceClient marketplaceClient;
    ErpClient erpClient;

    @Transactional
    public void syncQueue(SyncPayload payload) {
        if (AppType.MARKETPLACE == payload.getAppType()) {
            this.syncToMarketplace(payload);
        } else {
            this.sync(payload);
        }
    }

    private void syncToMarketplace(SyncPayload payload) {
        Company company = companyRepository.findByIdToSync(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", payload.getId()));

        List<Role> roles = List.of(Role.ROLE_BUSINESS_OWNER, Role.ROLE_MARKETPLACE_BUSINESS_OWNER);

        User user = userRepository.findAllByCompanyIdAndStatusAndRoleInAndDeletedIsFalse(company.getId(), UserStatus.ACTIVE, roles)
                .stream()
                .findFirst()
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "companyId", company.getId()));

        MarketplaceCompanySyncDTO marketplaceCompanySyncDTO = new MarketplaceCompanySyncDTO();
        marketplaceCompanySyncDTO.setAppType(AppType.SUPPLY.name());
        marketplaceCompanySyncDTO.setId(company.getId());
        marketplaceCompanySyncDTO.setName(company.getName());
        marketplaceCompanySyncDTO.setBrand(company.getBrand());
        marketplaceCompanySyncDTO.setOwnerRole(user.getRole().getCode());
        if (SecurityUtils.isTin(company.getTin())) {
            marketplaceCompanySyncDTO.setTin(company.getTin());
        } else {
            marketplaceCompanySyncDTO.setPinfl(company.getTin());
        }
        if (company.getCompanyBank() != null) {
            marketplaceCompanySyncDTO.setOked(company.getCompanyBank().getOked());
        }
        if (company.getVat() != null) {
            marketplaceCompanySyncDTO.setVatRate(company.getVat().getAmount());
        }
        if (company.getBusinessType() != null) {
            marketplaceCompanySyncDTO.setBusinessType(company.getBusinessType().getCode());
        }
        if (company.getActivityType() != null) {
            ActivityTypeSyncDTO activityTypeSyncDTO = new ActivityTypeSyncDTO();
            activityTypeSyncDTO.setCode(company.getActivityType().getCode());
            activityTypeSyncDTO.setParentCode(company.getActivityType().getParent().getCode());
            marketplaceCompanySyncDTO.setActivityType(activityTypeSyncDTO);
        }
        if (company.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            addressSyncDTO.setRegionCode(company.getAddress().getRegion().getCode());
            addressSyncDTO.setDistrictCode(company.getAddress().getDistrict().getCode());
            addressSyncDTO.setStreet(company.getAddress().getStreet());
            addressSyncDTO.setHouse(company.getAddress().getHouse());
            addressSyncDTO.setApartment(company.getAddress().getApartment());
            addressSyncDTO.setLongitude(company.getAddress().getLongitude());
            addressSyncDTO.setLatitude(company.getAddress().getLatitude());
            marketplaceCompanySyncDTO.setAddress(addressSyncDTO);
        }
        if (company.getMarketplaceId() == null) {
            company.setMarketplaceId(marketplaceClient.syncCompany(marketplaceCompanySyncDTO));
        } else {
            marketplaceClient.updateCompany(marketplaceCompanySyncDTO);
        }
        companyRepository.save(company);
    }

    private void sync(SyncPayload payload) {
        Company company = companyRepository.findByIdToSync(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", payload.getId()));
        CompanySyncDTO companySyncDTO = new CompanySyncDTO();
        companySyncDTO.setId(company.getId());
        companySyncDTO.setName(company.getName());
        companySyncDTO.setCompanyName(Optional.ofNullable(company.getBrand()).orElse(company.getName()));
        companySyncDTO.setPhone(company.getPhone());
        if (SecurityUtils.isTin(company.getTin())) {
            companySyncDTO.setTin(company.getTin());
        } else {
            companySyncDTO.setPinfl(company.getTin());
        }
        if (company.getCompanyBank() != null) {
            companySyncDTO.setOked(company.getCompanyBank().getOked());
        }
        if (company.getVat() != null) {
            companySyncDTO.setVatRate(company.getVat().getAmount());
        }
        if (company.getBusinessType() != null) {
            companySyncDTO.setBusinessType(company.getBusinessType().getCode());
        }
        if (company.getActivityType() != null) {
            ActivityTypeSyncDTO activityTypeSyncDTO = new ActivityTypeSyncDTO();
            activityTypeSyncDTO.setCode(company.getActivityType().getCode());
            activityTypeSyncDTO.setParentCode(company.getActivityType().getParent().getCode());
            companySyncDTO.setActivityType(activityTypeSyncDTO);
        }
        if (company.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            addressSyncDTO.setRegionCode(company.getAddress().getRegion().getCode());
            addressSyncDTO.setDistrictCode(company.getAddress().getDistrict().getCode());
            addressSyncDTO.setStreet(company.getAddress().getStreet());
            addressSyncDTO.setHouse(company.getAddress().getHouse());
            addressSyncDTO.setApartment(company.getAddress().getApartment());
            addressSyncDTO.setLongitude(company.getAddress().getLongitude());
            addressSyncDTO.setLatitude(company.getAddress().getLatitude());
            companySyncDTO.setAddress(addressSyncDTO);
        }
        if (company.getOwner() != null) {
            CompanyEmployeeSyncDTO directorDTO = new CompanyEmployeeSyncDTO();
            directorDTO.setId(company.getOwner().getId());
            directorDTO.setFirstName(company.getOwner().getFirstName());
            directorDTO.setLastName(company.getOwner().getLastName());
            directorDTO.setPatronymic(company.getOwner().getPatronymic());
            directorDTO.setPhone(company.getOwner().getPhone());
            directorDTO.setUsername(company.getOwner().getLogin());
            companySyncDTO.setDirector(directorDTO);

            if (!StringUtils.hasText(companySyncDTO.getPhone())) {
                companySyncDTO.setPhone(Optional.ofNullable(company.getOwner().getLogin()).orElse(company.getOwner().getPhone()));
            }
        }
        if (company.getCompanyBank() != null) {
            CompanyBankSyncDTO bankDTO = new CompanyBankSyncDTO();
            bankDTO.setMfo(company.getCompanyBank().getBank().getMfo());
            bankDTO.setAccountNumber(company.getCompanyBank().getAccountNumber());
            companySyncDTO.setBank(bankDTO);
        }
        if (AppType.ERP == payload.getAppType()) {
            Long erpId = erpClient.syncCompany(companySyncDTO);
            if (erpId != null) {
                company.setErpId(erpId);
                companyRepository.save(company);
            }
        }
    }

    @Transactional
    public void batchSyncToMarketplace(Company company) {
        MarketplaceCompanyBatchSyncDTO marketplaceCompanyBatchSyncDTO = new MarketplaceCompanyBatchSyncDTO();
        marketplaceCompanyBatchSyncDTO.setAppType(AppType.SUPPLY.getCode());

        MarketplaceCompanySyncDTO marketplaceCompanySyncDTO = new MarketplaceCompanySyncDTO();
        marketplaceCompanySyncDTO.setAppType(AppType.SUPPLY.getCode());
        marketplaceCompanySyncDTO.setId(company.getId());
        marketplaceCompanySyncDTO.setName(company.getName());
        if (SecurityUtils.isTin(company.getTin())) {
            marketplaceCompanySyncDTO.setTin(company.getTin());
        } else {
            marketplaceCompanySyncDTO.setPinfl(company.getTin());
        }
        marketplaceCompanySyncDTO.setBrand(company.getBrand());
        marketplaceCompanySyncDTO.setPhone(company.getPhone());

        if (company.getVat() != null) {
            marketplaceCompanySyncDTO.setVatRate(company.getVat().getAmount());
        }
        if (company.getBusinessType() != null) {
            marketplaceCompanySyncDTO.setBusinessType(company.getBusinessType().getCode());
        }
        if (company.getActivityTypeId() != null) {
            ActivityTypeSyncDTO activityTypeSyncDTO = new ActivityTypeSyncDTO();
            activityTypeSyncDTO.setCode(company.getActivityType().getCode());
            activityTypeSyncDTO.setParentCode(company.getActivityType().getParent().getCode());
            marketplaceCompanySyncDTO.setActivityType(activityTypeSyncDTO);
        }
        if (company.getAddress() != null) {
            AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
            addressSyncDTO.setRegionCode(company.getAddress().getRegion().getCode());
            addressSyncDTO.setDistrictCode(company.getAddress().getDistrict().getCode());
            addressSyncDTO.setStreet(company.getAddress().getStreet());
            addressSyncDTO.setHouse(company.getAddress().getHouse());
            addressSyncDTO.setApartment(company.getAddress().getApartment());
            addressSyncDTO.setLongitude(company.getAddress().getLongitude());
            addressSyncDTO.setLatitude(company.getAddress().getLatitude());
            marketplaceCompanySyncDTO.setAddress(addressSyncDTO);
        }
        marketplaceCompanyBatchSyncDTO.setCompany(marketplaceCompanySyncDTO);

        List<MarketplaceCustomerUserSyncDTO> users = new ArrayList<>();
        MarketplaceCustomerUserSyncDTO ownerDTO = new MarketplaceCustomerUserSyncDTO();
        ownerDTO.setId(company.getOwner().getId());
        ownerDTO.setAppType(AppType.SUPPLY.getCode());
        ownerDTO.setFirstName(company.getOwner().getFirstName());
        ownerDTO.setLastName(company.getOwner().getLastName());
        ownerDTO.setPatronymic(company.getOwner().getPatronymic());
        ownerDTO.setPhone(company.getOwner().getPhone());
        ownerDTO.setUsername(company.getOwner().getLogin());
        users.add(ownerDTO);

        if (company.getCompanyBank() != null) {
            MarketplaceCompanyBankSyncDTO marketplaceCompanyBankSyncDTO = new MarketplaceCompanyBankSyncDTO();
            marketplaceCompanyBankSyncDTO.setId(company.getCompanyBank().getId());
            marketplaceCompanyBankSyncDTO.setAppType(AppType.SUPPLY.getCode());
            marketplaceCompanyBankSyncDTO.setCompanyId(company.getId());
            marketplaceCompanyBankSyncDTO.setMfo(company.getCompanyBank().getBank().getMfo());
            marketplaceCompanyBankSyncDTO.setAccountNumber(company.getCompanyBank().getAccountNumber());
            marketplaceCompanyBatchSyncDTO.setBank(marketplaceCompanyBankSyncDTO);
        }

        List<MarketplaceBranchSyncDTO> marketplaceBranchSyncDTOList = branchRepository.findAllToSync(company.getId(), BranchType.RETAILER).stream().map(branch -> {
            MarketplaceBranchSyncDTO marketplaceBranchSyncDTO = new MarketplaceBranchSyncDTO();
            marketplaceBranchSyncDTO.setId(branch.getId());
            marketplaceBranchSyncDTO.setAppType(AppType.SUPPLY.getCode());
            marketplaceBranchSyncDTO.setCompanyId(branch.getCompanyId());
            marketplaceBranchSyncDTO.setName(branch.getName());
            if (branch.getAddress() != null) {
                AddressSyncDTO addressSyncDTO = new AddressSyncDTO();
                if (branch.getAddress().getRegion() != null) {
                    addressSyncDTO.setRegionCode(branch.getAddress().getRegion().getCode());
                }
                if (branch.getAddress().getDistrict() != null) {
                    addressSyncDTO.setDistrictCode(branch.getAddress().getDistrict().getCode());
                }
                addressSyncDTO.setStreet(branch.getAddress().getStreet());
                addressSyncDTO.setHouse(branch.getAddress().getHouse());
                addressSyncDTO.setApartment(branch.getAddress().getApartment());
                addressSyncDTO.setLongitude(branch.getAddress().getLongitude());
                addressSyncDTO.setLatitude(branch.getAddress().getLatitude());
                marketplaceBranchSyncDTO.setAddress(addressSyncDTO);
            }
            return marketplaceBranchSyncDTO;
        }).toList();
        marketplaceCompanyBatchSyncDTO.setBranches(marketplaceBranchSyncDTOList);
        company.setMarketplaceId(marketplaceClient.syncCompanyBatch(marketplaceCompanyBatchSyncDTO));
        companyRepository.save(company);
    }
}