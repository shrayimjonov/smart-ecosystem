package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyDetailAdminDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyListAdminDTO;
import uz.uzkassa.cts.dto.company.base.CompanyCheckDTO;
import uz.uzkassa.cts.dto.company.base.CompanyDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.AddressService;
import uz.uzkassa.cts.service.CompanyService;
import uz.uzkassa.cts.service.ContactService;
import uz.uzkassa.cts.service.SocialAddressService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CompanyServiceImpl extends BaseServiceImpl<Company, Long> implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ReferenceRepository referenceRepository;
    private final ContactService contactService;
    private final SocialAddressService socialAddressService;
    private final SoliqClient soliqClient;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final VatRepository vatRepository;
    private final AddressMapper addressMapper;
    private final AddressService addressService;
    private final ValidationContext validationContext;

    public CompanyServiceImpl(CompanyRepository companyRepository, ReferenceRepository referenceRepository, ContactService contactService, SocialAddressService socialAddressService, SoliqClient soliqClient, RegionRepository regionRepository, DistrictRepository districtRepository, VatRepository vatRepository, AddressMapper addressMapper, AddressService addressService, ValidationContext validationContext) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.referenceRepository = referenceRepository;
        this.contactService = contactService;
        this.socialAddressService = socialAddressService;
        this.soliqClient = soliqClient;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.vatRepository = vatRepository;
        this.addressMapper = addressMapper;
        this.addressService = addressService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CompanyDTO companyDTO) {
        validationContext.validate(companyDTO);

        String phone = CollectionUtils.isEmpty(companyDTO.getPhones()) ? null : companyDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        String email = CollectionUtils.isEmpty(companyDTO.getEmails()) ? null : companyDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();

        Company company = new Company();
        company.setName(companyDTO.getName());
        company.setBrand(companyDTO.getBrand());
        company.setTin(companyDTO.getTin());
        if (companyDTO.getVatId() != null && companyDTO.getVatId() > 0) {
            company.setVatId(companyDTO.getVatId());
        }
        company.setOked(companyDTO.getOked());
        company.setPhone(phone);
        company.setEmail(email);
        companyRepository.save(company);

        if (!CollectionUtils.isEmpty(companyDTO.getPhones())) {
            contactService.createBatch(company.getEntityType(), company.getEntityId(), companyDTO.getPhones());
        }
        if (!CollectionUtils.isEmpty(companyDTO.getEmails())) {
            contactService.createBatch(company.getEntityType(), company.getEntityId(), companyDTO.getEmails());
        }
        if (!CollectionUtils.isEmpty(companyDTO.getSocialAddresses())) {
            socialAddressService.createBatch(company.getEntityType(), company.getEntityId(), companyDTO.getSocialAddresses());
        }
        if (!CollectionUtils.isEmpty(companyDTO.getAddresses())) {
            addressService.createBatch(company.getEntityType(), company.getEntityId(), companyDTO.getAddresses());
        }
        return company.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CompanyDTO companyDTO) {
        companyDTO.setId(id);
        validationContext.validate(companyDTO);
        String phone = CollectionUtils.isEmpty(companyDTO.getPhones()) ? null : companyDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        String email = CollectionUtils.isEmpty(companyDTO.getEmails()) ? null : companyDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(companyDTO.getName());
                    company.setBrand(companyDTO.getBrand());
                    company.setTin(companyDTO.getTin());
                    if (companyDTO.getVatId() != null && !Objects.equals(company.getVatId(), companyDTO.getVatId())) {
                        company.setVat(vatRepository.findById(companyDTO.getVatId()).orElseThrow(validationService.entityNotFoundExceptionThrow(VAT)));
                    }
                    company.setVatId(companyDTO.getVatId());
                    company.setOked(companyDTO.getOked());
                    company.setPhone(phone);
                    company.setEmail(email);
                    if (!CollectionUtils.isEmpty(companyDTO.getPhones())) {
                        contactService.updateBatch(company.getEntityType(), company.getEntityId(), companyDTO.getPhones());
                    }
                    if (!CollectionUtils.isEmpty(companyDTO.getEmails())) {
                        contactService.updateBatch(company.getEntityType(), company.getEntityId(), companyDTO.getEmails());
                    }
                    if (!CollectionUtils.isEmpty(companyDTO.getSocialAddresses())) {
                        socialAddressService.updateBatch(company.getEntityType(), company.getEntityId(), companyDTO.getSocialAddresses());
                    }
                    if (!CollectionUtils.isEmpty(companyDTO.getAddresses())) {
                        addressService.updateBatch(company.getEntityType(), company.getEntityId(), companyDTO.getAddresses());
                    }
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

    }

    @Override
    public Page<CompanyListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Company> resultList = companyRepository.getResultList(filter);
        List<CompanyListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(company -> {
                    CompanyListAdminDTO companyListDTO = new CompanyListAdminDTO();
                    companyListDTO.setId(company.getId());
                    companyListDTO.setName(company.getName());
                    companyListDTO.setTin(company.getTin());
                    companyListDTO.setPhone(company.getPhone());
                    companyListDTO.setStatus(company.getStatus());
                    companyListDTO.setAddress(addressMapper.toShortDTO(addressService.getLegalAddress(company.getEntityId(), company.getEntityType())));
                    return companyListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CompanyCommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return companyRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Company::toShortDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDetailAdminDTO get(Long id) {
        return companyRepository
                .findById(id)
                .map(company -> {
                    CompanyDetailAdminDTO companyDetailDTO = new CompanyDetailAdminDTO();
                    companyDetailDTO.setId(company.getId());
                    companyDetailDTO.setName(company.getName());
                    companyDetailDTO.setBrand(company.getBrand());
                    companyDetailDTO.setTin(company.getTin());
                    companyDetailDTO.setOked(company.getOked());
                    if (company.getVatId() != null) {
                        companyDetailDTO.setVat(company.getVat().toDTO());
                    }

                    List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
                    List<Long> emailTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.EMAIL_TYPE);

                    companyDetailDTO.setPhones(contactService.getList(company.getEntityType(), company.getEntityId(), phoneTypeIds));
                    companyDetailDTO.setEmails(contactService.getList(company.getEntityType(), company.getEntityId(), emailTypeIds));
                    companyDetailDTO.setSocialAddresses(socialAddressService.getByEntityTypeAndEntityId(company.getEntityType(), company.getEntityId()));

                    return companyDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        companyRepository
                .findById(id)
                .map(company -> {
                    company.setStatus(status);
                    companyRepository.save(company);
                    return company.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
    }

    @Override
    public CompanyCheckDTO check(String tin) {
        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(tin);
        CompanyCheckDTO companyCheckDTO = new CompanyCheckDTO();
        companyCheckDTO.setName(soliqCompanyDTO.getName());
        companyCheckDTO.setTin(soliqCompanyDTO.getTin());
        companyCheckDTO.setPinfl(soliqCompanyDTO.getPersonalNum());

        if (soliqCompanyDTO.getNs10Code() != null) {
            Region region = regionRepository.findByCode(soliqCompanyDTO.getNs10Code());
            if (region != null) {
                BaseAddressDetailDTO baseAddressDetailDTO = new BaseAddressDetailDTO();
                baseAddressDetailDTO.setRegion(region.toCommonDTO());
                if (soliqCompanyDTO.getNs11Code() != null) {
                    districtRepository.findFirstByRegionIdAndCode(region.getId(), soliqCompanyDTO.getNs11Code()).ifPresent(district -> baseAddressDetailDTO.setDistrict(district.toCommonDTO()));
                }
                baseAddressDetailDTO.setStreet(soliqCompanyDTO.getAddress());
                companyCheckDTO.setAddress(baseAddressDetailDTO);
            }
        }
        companyCheckDTO.setOked(soliqCompanyDTO.getOked());
        companyCheckDTO.setAccountNumber(soliqCompanyDTO.getAccount());
        companyCheckDTO.setAccountant(soliqCompanyDTO.getAccountant());
//        if (StringUtils.isNotEmpty(soliqCompanyDTO.getMfo())) {
//            bankRepository.findByCode(soliqCompanyDTO.getMfo()).ifPresent(bank -> companyCheckDTO.setBank(bank.toCommonCodeDTO()));
//        }
        if (soliqCompanyDTO.getStatusCode() != null) {
            companyCheckDTO.setStatus(new CommonDTO(Long.valueOf(soliqCompanyDTO.getStatusCode()), soliqCompanyDTO.getStatusName()));
        }
        if (StringUtils.isNotEmpty(soliqCompanyDTO.getDirector())) {
            companyCheckDTO.setDirector(new CompanyCommonDTO(null, soliqCompanyDTO.getDirector(), soliqCompanyDTO.getDirectorTin(), soliqCompanyDTO.getDirectorPinfl()));
        }
        return companyCheckDTO;
    }
}
