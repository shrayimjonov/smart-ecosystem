package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyBank;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.RevenueByRegionDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDetailDTO;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.repository.CompanyBankRepository;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;

import java.util.List;
import java.util.Optional;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierService extends BaseService {

    CompanyBankService companyBankService;
    CompanyBankRepository companyBankRepository;
    PartnerRepository partnerRepository;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    CacheService cacheService;
    ValidationContext validationContext;

    @Transactional
    public Company create(SupplierDTO supplierDTO) {
        validationContext.validate(supplierDTO);
        Company company = null;
        if (StringUtils.isNotEmpty(supplierDTO.getCompanyTin())) {
            company = companyRepository.findFirstByTinAndDeletedIsFalse(supplierDTO.getCompanyTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", supplierDTO.getCompanyTin()));
        }
        Long companyId = company != null ? company.getId() : Optional.ofNullable(supplierDTO.getCompanyId()).orElse(getCurrentCompanyId());
        if (companyId == null) {
            throw validationService.badRequestExceptionThrow("Company is required").get();
        }
        Company supplier = companyRepository.findFirstByTinAndDeletedIsFalse(supplierDTO.getTin()).orElseGet(Company::new);
        supplier.setName(supplierDTO.getName());
        supplier.setTin(supplierDTO.getTin());
        if (StringUtils.isEmpty(supplier.getTin()) && StringUtils.isNotEmpty(supplierDTO.getTin())) {
            supplier.setTin(supplierDTO.getTin());
        }
        supplier.setContactPerson(supplierDTO.getContactPerson());
        supplier.setPhone(supplierDTO.getPhone());
        if (supplierDTO.getAddress() != null) {
            if (supplier.isNew()) {
                supplier.setAddress(addressMapper.toEntity(supplierDTO.getAddress()));
            } else {
                supplier.setAddress(
                    addressMapper.merge(supplierDTO.getAddress(), Optional.ofNullable(supplier.getAddress()).orElse(new Address()))
                );
            }
        }
        supplier.setDirector(supplierDTO.getDirector());
        supplier.setAccountant(supplierDTO.getAccountant());
        companyRepository.save(supplier);

        if (supplierDTO.getBank() != null) {
            CompanyBank companyBank = Optional.ofNullable(supplier.getCompanyBank()).orElse(new CompanyBank());
            companyBank.setBankId(supplierDTO.getBank().getBankId());
            companyBank.setCompanyId(supplier.getId());
            companyBank.setAccountNumber(supplierDTO.getBank().getAccountNumber());
            companyBank.setOked(supplierDTO.getBank().getOked());
            companyBank.setMain(Optional.of(supplierDTO.getBank().getMain()).orElse(true));
            companyBankRepository.save(companyBank);
            supplier.setCompanyBankId(companyBank.getId());
        }
        companyRepository.save(supplier);
        cacheService.evictCompany(supplier.getTin());
        if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(supplier.getId(), companyId, companyId)) {
            partnerRepository.save(new Partners(supplier.getId(), companyId, companyId));
        }
        return supplier;
    }

    @Transactional
    public Long update(Long id, SupplierDTO supplierDTO) {
        validationContext.validate(supplierDTO);
        return companyRepository.findById(id).map(supplier -> {
            supplier.setName(supplierDTO.getName());
            supplier.setPhone(supplierDTO.getPhone());
            supplier.setContactPerson(supplierDTO.getContactPerson());
            supplier.setDirector(supplierDTO.getDirector());
            supplier.setAccountant(supplierDTO.getAccountant());
            if (supplierDTO.getAddress() != null) {
                Address address = addressMapper.toEntity(supplierDTO.getAddress());
                if (supplierDTO.getAddress().getRegionId() != null) {
                    Region region = regionRepository.findById(supplierDTO.getAddress().getRegionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "id", supplierDTO.getAddress().getRegionId()));
                    address.setRegion(region);
                }
                if (supplierDTO.getAddress().getDistrictId() != null) {
                    District district = districtRepository.findById(supplierDTO.getAddress().getDistrictId()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "id", supplierDTO.getAddress().getDistrictId()));
                    address.setDistrict(district);
                }
                supplier.setAddress(addressMapper.merge(supplierDTO.getAddress(), address));
            }
            if (supplierDTO.getBank() != null && supplierDTO.getBank().getBankId() != null) {
                CompanyBank companyBank = Optional.ofNullable(supplier.getCompanyBank()).orElse(new CompanyBank());
                companyBank.setBankId(supplierDTO.getBank().getBankId());
                companyBank.setCompanyId(supplier.getId());
                companyBank.setAccountNumber(supplierDTO.getBank().getAccountNumber());
                companyBank.setOked(supplierDTO.getBank().getOked());
                companyBank.setMain(Optional.of(supplierDTO.getBank().getMain()).orElse(true));
                companyBankRepository.save(companyBank);
                supplier.setCompanyBankId(companyBank.getId());
            }
            companyRepository.save(supplier);
            cacheService.evictCompany(supplier.getTin());
            return supplier.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public SupplierDetailDTO get(Long id) {
        return companyRepository.findById(id).map(supplier -> {
            SupplierDetailDTO supplierDetailDTO = new SupplierDetailDTO();
            supplierDetailDTO.setId(supplier.getId());
            supplierDetailDTO.setName(supplier.getName());
            supplierDetailDTO.setTin(supplier.getTin());
            supplierDetailDTO.setAddress(addressMapper.toDetailDTO(supplier.getAddress()));
            supplierDetailDTO.setPhone(supplier.getPhone());
            supplierDetailDTO.setContactPerson(supplier.getContactPerson());
            supplierDetailDTO.setDirector(supplier.getDirector());
            supplierDetailDTO.setAccountant(supplier.getAccountant());
            supplierDetailDTO.setBankDetail(companyBankService.get(supplier));
            return supplierDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", id));
    }

    public Page<SupplierListDTO> getList(CompanyFilter filter) {
        ResultList<Partners> resultList = partnerRepository.getResultList(filter, true);
        List<SupplierListDTO> result = resultList
            .getList()
            .stream()
            .map(partner -> {
                SupplierListDTO supplierListDTO = new SupplierListDTO();
                supplierListDTO.setId(partner.getSupplier().getId());
                supplierListDTO.setName(partner.getSupplier().getName());
                supplierListDTO.setTin(partner.getSupplier().getTin());
                supplierListDTO.setContactPerson(partner.getSupplier().getContactPerson());
                supplierListDTO.setPhone(partner.getSupplier().getPhone());
                supplierListDTO.setAddress(addressMapper.toDetailDTO(partner.getSupplier().getAddress()));
                return supplierListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CompanyBaseDTO> lookUp(CompanyFilter filter) {
        return partnerRepository.getResultList(filter, true)
            .getList()
            .stream()
            .map(partner -> partner.getSupplier().toCommonBaseDTO())
            .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        partnerRepository.deleteBySupplierIdAndCustomerId(id, getCurrentCompanyId());
    }

    public List<RevenueByRegionDTO> getRevenueByRegion() {
        return companyRepository.getRevenueByRegion();
    }
}
