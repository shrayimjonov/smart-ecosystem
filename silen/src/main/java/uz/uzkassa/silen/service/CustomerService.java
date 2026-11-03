package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.dto.CustomerProductDTO;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.dto.invoice.CompanyVatDTO;
import uz.uzkassa.silen.dto.invoice.FacturaCustomer;
import uz.uzkassa.silen.dto.invoice.TINAbstractDTO;
import uz.uzkassa.silen.dto.invoice.VATCompanyDateDTO;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.dto.warehouse.CustomerCommonDTO;
import uz.uzkassa.silen.dto.warehouse.CustomerDTO;
import uz.uzkassa.silen.dto.warehouse.FinancialDiscountDTO;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.SoliqClient;
import uz.uzkassa.silen.repository.pg.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService extends BaseService {
    CustomerRepository customerRepository;
    CustomerProductRepository customerProductRepository;
    ProductRepository productRepository;
    BankRepository bankRepository;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    SoliqClient soliqClient;
    private final ExportReportService exportReportService;

    @Transactional
    public CustomerDTO save(final CustomerDTO dto) {
        final String organizationId = getCurrentOrganizationId();
        final Organization organization = organizationRepository.findById(organizationId).orElseThrow(notFound());

//        if (dto.getTin().equalsIgnoreCase(organization.getTin())) {
//            throw new BadRequestException("Невозможно добавить свою компанию как клиент");
//        }
        if (dto.getId() != null) {
            customerRepository.findFirstByIdNotAndOrganizationIdAndTinAndDeletedIsFalse(dto.getId(), organizationId, dto.getTin()).ifPresent(x -> {
                throw badRequest("Клиент с таким ИНН существует").get();
            });
        } else {
            customerRepository.findFirstByOrganizationIdAndTinAndDeletedIsFalse(organizationId, dto.getTin()).ifPresent(x -> {
                throw new BadRequestException("Клиент с таким ИНН существует");
            });
        }

        Customer customer = Optional.ofNullable(dto.getId()).flatMap(customerRepository::findById).orElse(new Customer());
        customer.setOrganizationId(organizationId);
        customer.setTin(dto.getTin());
        customer.setName(dto.getName());
        customer.setHasFinancialDiscount(dto.isHasFinancialDiscount());
        customer.setCommittent(dto.isCommittent());
        customer.setComission(dto.getComission());

        customer.setAccount(dto.getAccount());
        customer.setAddress(dto.getAddress());
        customer.setMobile(dto.getMobile());
        customer.setWorkPhone(dto.getWorkPhone());
        customer.setOked(dto.getOked());
        customer.setDirector(dto.getDirector());
        customer.setAccountant(dto.getAccountant());
        customer.setDistrictId(dto.getDistrictId());
        customer.setBankId(dto.getBankId());
        customerRepository.saveAndFlush(customer);
        if (dto.getId() == null) {
            rabbitMqProducer.sendCustomerProduct(new Payload(customer.getOrganizationId(), customer.getId()));
        }
        return customer.toDto();
    }

    public Page<CustomerDTO> findAll(CustomerFilter filter) {
        return customerRepository.findAllByFilter(filter).map(Customer::toDto);
    }

    public CustomerDTO findOne(Long id) {
        return customerRepository.findById(id)
                .map(Customer::toDto)
                .orElseThrow(notFound());
    }

    @Transactional
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    public List<CustomerCommonDTO> getItems(CustomerFilter filter) {
        return customerRepository.findAllByFilter(filter)
                .map(customer -> {
                    CustomerCommonDTO customerCommonDTO = new CustomerCommonDTO();
                    customerCommonDTO.setId(customer.getId());
                    customerCommonDTO.setName(customer.getName());
                    customerCommonDTO.setTin(customer.getTin());
                    customerCommonDTO.setDescription(String.valueOf(customer.isCommittent()));
                    return customerCommonDTO;
                }).getContent();
    }

    public FacturaCustomer getCustomer(final String tin, final boolean forInvoice) {
        final TINAbstractDTO companyDTO = soliqClient.getCustomerByTin(tin);
        if (companyDTO != null && StringUtils.isNotBlank(companyDTO.getTin())) {
            FacturaCustomer dto = new FacturaCustomer();
            dto.setTin(companyDTO.getTin());
            dto.setName(companyDTO.getName());
            dto.setAccount(companyDTO.getAccount());
            dto.setBankId(companyDTO.getMfo());
            if (companyDTO.getMfo() != null && bankRepository.existsById(companyDTO.getMfo())) {
                Optional<Bank> bank = bankRepository.findById(companyDTO.getMfo());
                bank.ifPresent(bank1 -> dto.setBankName(bank1.getName()));
            }
            dto.setAddress(companyDTO.getAddress());
            dto.setMobile(companyDTO.getMobile());
            dto.setWorkPhone(companyDTO.getWorkPhone());
            dto.setOked(companyDTO.getOked());
            if (companyDTO.getNs10Code() != null && companyDTO.getNs11Code() != null) {
                Optional<Region> region = regionRepository.findById(companyDTO.getNs10Code());

                region.ifPresent(region1 -> {
                    dto.setRegionId(region1.getId());
                    dto.setRegionName(region1.getNameLocale());
                });
                Optional<District> district = districtRepository.findFirstByCodeAndRegionId(companyDTO.getNs11Code(), companyDTO.getNs10Code());
                if (district.isPresent()) {
                    dto.setDistrictId(district.get().getId());
                    dto.setDistrictName(district.get().getNameLocale());
                }
            }
            dto.setDirector(companyDTO.getDirector());
            dto.setAccountant(companyDTO.getAccountant());
            if (forInvoice) {
                VATCompanyDateDTO vatCompanyDTOResponse = soliqClient.getCompanyVatByDate(dto.getTin());
                if (vatCompanyDTOResponse != null && vatCompanyDTOResponse.getData() != null) {
                    CompanyVatDTO vatCompanyDTO = vatCompanyDTOResponse.getData();
                    dto.setVatRegCode(vatCompanyDTO.getVatRegCode());
                    dto.setVatRegStatus(vatCompanyDTO.getStatus());
                }
//                dto.setTaxGap(facturaClient.getCompanyTaxGap(dto.getTin()));
            }
            return dto;
        } else {
            throw new BadRequestException("Company not found");
        }
    }

    @Transactional
    public CustomerProductDTO updateDiscount(CustomerProductDTO dto) {
        CustomerProduct product = customerProductRepository.findById(new CustomerProductId(dto.getCustomerId(), dto.getProductId()))
                .orElseThrow(notFound());
        product.setDiscountPercent(dto.getDiscountPercent());
        product.setDiscount(dto.getDiscount());
        customerProductRepository.save(product);
        return product.toDto();
    }

    @Transactional
    public void updateDiscountAll(CustomerProductDTO dto) {
        customerProductRepository.updateDiscount(dto.getCustomerId(), dto.getDiscountPercent(), dto.getDiscount());
    }

    @Transactional
    public void updateFinancialDiscount(FinancialDiscountDTO dto) {
        customerRepository.updateFinancialDiscount(dto.getCustomerId(), dto.isHasFinancialDiscount());
    }

    public Page<CustomerProductDTO> findAllProducts(CustomerFilter filter) {
        return customerProductRepository.findAllByFilter(filter)
                .map(CustomerProduct::toDto);
    }

    @Transactional
    public void createProduct(String organizationId, String productId) {
        List<Long> customerIds = customerRepository.findAllIds(organizationId);
        List<CustomerProduct> products = new ArrayList<>();
        for (Long customerId : customerIds) {
            products.add(new CustomerProduct(customerId, productId));
            if (products.size() == 50) {
                customerProductRepository.saveAll(products);
                products.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(products)) {
            customerProductRepository.saveAll(products);
        }
    }

    @Transactional
    public void createProducts(String organizationId, Long customerId) {
        List<String> productIds = productRepository.findAllIds(organizationId);
        List<String> existingProductIds = customerProductRepository.findAllIds(customerId);
        productIds.removeAll(existingProductIds);
        List<CustomerProduct> products = new ArrayList<>();
        for (String productId : productIds) {
            products.add(new CustomerProduct(customerId, productId));
            if (products.size() == 50) {
                customerProductRepository.saveAll(products);
                products.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(products)) {
            customerProductRepository.saveAll(products);
        }
    }

    public void migrateProducts() {
        List<Customer> customers = customerRepository.findAllByDeletedIsFalse();
        for (Customer customer : customers) {
            rabbitMqProducer.sendCustomerProduct(new Payload(customer.getOrganizationId(), customer.getId()));
        }
    }

    @Transactional
    public CustomerDTO syncFromNIC(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(notFound());
        FacturaCustomer facturaCustomer = getCustomer(customer.getTin(), false);
        customer.setName(facturaCustomer.getName());
        customer.setDirector(facturaCustomer.getDirector());
        customer.setOked(facturaCustomer.getOked());
        customer.setBankId(facturaCustomer.getBankId());
        customer.setAccount(facturaCustomer.getAccount());
        customer.setAccountant(facturaCustomer.getAccountant());
        customer.setMobile(facturaCustomer.getMobile());
        customer.setWorkPhone(facturaCustomer.getWorkPhone());
        customer.setDistrictId(facturaCustomer.getDistrictId());
        customer.setAddress(facturaCustomer.getAddress());
        customerRepository.save(customer);
        return customer.toDto();
    }

    public void discountProductsExcel(Long customerId) {
        CustomerFilter customerFilter = new CustomerFilter();
        customerFilter.setCustomerId(customerId);
        exportReportService.export(customerFilter, ExportReportType.EXCEL_CUSTOMER_DISCOUNT, FileType.EXCEL, "CustomerDiscountExcel");
    }
}
