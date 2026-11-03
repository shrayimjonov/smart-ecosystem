package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.CustomerBranchScanner;
import uz.uzkassa.cts.domain.CustomerBranchScanner_;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.customerBranchScanner.CustomerBranchScannerDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdScannerDTO;
import uz.uzkassa.cts.filter.CBScannerFilter;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.mappings.CustomerBranchScannerMapper;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.CustomerBranchScannerRepository;
import uz.uzkassa.cts.service.CustomerBranchScannerService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.cts.utils.PredicateUtil.with;

@Transactional
@Service
public class CustomerBranchScannerServiceImpl extends BaseServiceImpl<CustomerBranchScanner, Long> implements CustomerBranchScannerService {

    private final CustomerBranchScannerRepository customerBranchScannerRepository;
    private final OfdClient ofdClient;
    private final CustomerBranchScannerMapper customerBranchScannerMapper;

    public CustomerBranchScannerServiceImpl(BaseRepository<CustomerBranchScanner, Long> repository,
                                            CustomerBranchScannerRepository customerBranchScannerRepository,
                                            OfdClient ofdClient, CustomerBranchScannerMapper customerBranchScannerMapper) {
        super(repository);
        this.customerBranchScannerRepository = customerBranchScannerRepository;
        this.ofdClient = ofdClient;
        this.customerBranchScannerMapper = customerBranchScannerMapper;
    }

    @Override
    public Long create(CustomerBranchScannerDTO scannerDTO) {
        CustomerBranchScanner customerBranchScanner = new CustomerBranchScanner();
        customerBranchScanner.setCode(scannerDTO.getCode());
        customerBranchScanner.setName(scannerDTO.getName());
        save(customerBranchScanner);
        return customerBranchScanner.getId();
    }

    @Override
    public Long update(Long id, CustomerBranchScannerDTO scannerDTO) {
        return customerBranchScannerRepository
                .findById(id)
                .map(customerBranchScanner -> {
                    customerBranchScanner.setCode(scannerDTO.getCode());
                    customerBranchScanner.setName(scannerDTO.getName());
                    save(customerBranchScanner);
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(SCANNER));
    }

    @Override
    public Page<CommonCodeDTO> list(CBScannerFilter filter) {
        return customerBranchScannerRepository
                .findAll(getSpecification(filter), filter.getOrderedPageable())
                .map(customerBranchScannerMapper::toDTO);
    }

    @Override
    public List<CommonCodeDTO> lookUp(CBScannerFilter filter) {
        return customerBranchScannerRepository
                .findAll(getSpecification(filter))
                .stream().map(customerBranchScannerMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static Specification<CustomerBranchScanner> getSpecification(CBScannerFilter filter) {
        return (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            Predicate and = builder.and(Stream.of(
                    with(filter::getCode, a -> builder.equal(root.get(CustomerBranchScanner_.CODE), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            Predicate or = builder.or(Stream.of(
                    with(filter::getSearch, a -> builder.like(builder.lower(root.get(CustomerBranchScanner_.NAME)), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            return builder.or(and, or);
        };
    }

    @Override
    public CommonCodeDTO get(Long id) {
        return customerBranchScannerRepository
                .findById(id).map(CustomerBranchScanner::toCommonCodeDTO)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(SCANNER));
    }

    @Override
    public void sync() {
        List<OfdScannerDTO> scannerDTOS = ofdClient.getScannerList().getData();
        if (scannerDTOS != null) {
            List<CustomerBranchScanner> scannerList = scannerDTOS
                    .stream()
                    .map(ofdScannerDTO -> {
                        CustomerBranchScanner customerBranchScanner =
                                customerBranchScannerRepository
                                        .findByCode(ofdScannerDTO.getCode().toString())
                                        .orElse(new CustomerBranchScanner());
                        customerBranchScanner.setCode(ofdScannerDTO.getCode().toString());
                        customerBranchScanner.setName(ofdScannerDTO.getName());
                        return customerBranchScanner;
                    }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(scannerList)) {
                ListUtils.partition(scannerList, 50).forEach(customerBranchScanners -> {
                    customerBranchScannerRepository.saveAll(customerBranchScanners);
                    customerBranchScannerRepository.flush();
                });
            }
        }
    }
}
