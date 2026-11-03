package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.company.Partner;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierDetailDTO;
import uz.uzkassa.horeca.dto.supplier.SupplierListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.integration.CtsClient;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.repository.CompanyRepository;
import uz.uzkassa.horeca.repository.PartnerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SupplierService
    extends BaseService
    implements CrudAdvanced<Long, SupplierDTO, SupplierDetailDTO, SupplierListDTO, CompanyFilter, CommonStatus, CompanyCommonDTO> {

    CompanyRepository companyRepository;
    PartnerRepository partnerRepository;
    CtsClient ctsClient;

    @Override
    public void validate(SupplierDTO supplierDTO) {
        if (StringUtils.isEmpty(supplierDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (StringUtils.isEmpty(supplierDTO.getTin())) {
            throw badRequestExceptionThrow("Tin is required").get();
        }
        if (StringUtils.isEmpty(supplierDTO.getContactName())) {
            throw badRequestExceptionThrow("Contact name is required").get();
        }
        if (StringUtils.isEmpty(supplierDTO.getContactPhone())) {
            throw badRequestExceptionThrow("Contact phone is required").get();
        }
    }

    public SupplierDTO check(String tin) {
        SoliqCompanyDTO soliqCompanyDTO = ctsClient.getSoliqCompany(tin);
        log.info("Check company Soliq response: " + soliqCompanyDTO);
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName(soliqCompanyDTO.getName());
        supplierDTO.setTin(tin);
        if (StringUtils.isNotEmpty(soliqCompanyDTO.getFirstName()) && StringUtils.isNotEmpty(soliqCompanyDTO.getLastName())) {
            supplierDTO.setContactName(soliqCompanyDTO.getFirstName() + " " + soliqCompanyDTO.getLastName());
        }
        supplierDTO.setContactPhone(soliqCompanyDTO.getWorkPhone());
        return supplierDTO;
    }

    @Override
    @Transactional
    public Long create(SupplierDTO supplierDTO) {
        this.validate(supplierDTO);
        Long companyId = getCurrentCompanyId();
        Company supplier = companyRepository.findByTin(supplierDTO.getTin()).orElse(new Company());
        supplier.setTin(supplierDTO.getTin());
        supplier.setName(supplierDTO.getName());
        supplier.setContactName(supplierDTO.getContactName());
        supplier.setContactPhone(supplierDTO.getContactPhone());
        companyRepository.save(supplier);
        if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(supplier.getId(), companyId, companyId)) {
            partnerRepository.save(new Partner(supplier.getId(), companyId, companyId));
        }
        return supplier.getId();
    }

    @Override
    public SupplierDetailDTO get(Long id) {
        return companyRepository
            .findById(id)
            .map(supplier -> {
                SupplierDetailDTO supplierDetailDTO = new SupplierDetailDTO();
                supplierDetailDTO.setId(supplier.getId());
                supplierDetailDTO.setName(supplier.getName());
                supplierDetailDTO.setTin(supplier.getTin());
                supplierDetailDTO.setContactName(supplier.getContactName());
                supplierDetailDTO.setContactPhone(supplier.getContactPhone());
                return supplierDetailDTO;
            })
            .orElseThrow(notFoundExceptionThrow(Partner.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public Long update(Long id, SupplierDTO supplierDTO) {
        return companyRepository
            .findById(id)
            .map(supplier -> {
                supplierDTO.setId(id);
                this.validate(supplierDTO);
                supplier.setName(supplierDTO.getName());
                supplier.setContactName(supplierDTO.getContactName());
                supplier.setContactPhone(supplierDTO.getContactPhone());
                companyRepository.save(supplier);
                return supplier.getId();
            })
            .orElseThrow(notFoundExceptionThrow(Partner.class.getSimpleName(), "id", id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        partnerRepository.deleteBySupplierIdAndCustomerId(id, getCurrentCompanyId());
    }

    @Override
    public Page<SupplierListDTO> getList(CompanyFilter filter) {
        ResultList<Partner> resultList = partnerRepository.getResultList(filter, true);
        List<SupplierListDTO> result = resultList
            .getList()
            .stream()
            .map(partner -> {
                SupplierListDTO supplierListDTO = new SupplierListDTO();
                supplierListDTO.setId(partner.getSupplier().getId());
                supplierListDTO.setName(partner.getSupplier().getName());
                supplierListDTO.setTin(partner.getSupplier().getTin());
                supplierListDTO.setContactName(partner.getSupplier().getContactName());
                supplierListDTO.setContactPhone(partner.getSupplier().getContactPhone());
                return supplierListDTO;
            })
            .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    public List<CompanyCommonDTO> lookUp(CompanyFilter filter) {
        return partnerRepository
            .getResultList(filter, true)
            .getList()
            .stream()
            .map(partner -> toCompanyBaseDTO(partner.getSupplier()))
            .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, CommonStatus status) {
        partnerRepository
            .findById(id)
            .map(partner -> {
                partner.setStatus(status);
                partnerRepository.save(partner);
                return partner.getId();
            })
            .orElseThrow(notFoundExceptionThrow(Partner.class.getSimpleName(), "id", id));
    }
}
