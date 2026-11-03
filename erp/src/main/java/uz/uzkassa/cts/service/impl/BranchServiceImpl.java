package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.branch.BranchDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchDetailAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchListAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchStatisticsAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.BranchService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class BranchServiceImpl extends BaseServiceImpl<Branch, Long> implements BranchService {

    private final BranchRepository branchRepository;
    private final AddressMapper addressMapper;
    private final CustomerRepository customerRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final BranchFiscalModuleRepository branchFiscalModuleRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidationContext validationContext;

    public BranchServiceImpl(BranchRepository branchRepository, AddressMapper addressMapper, CustomerRepository customerRepository, CustomerBranchRepository customerBranchRepository, BranchDeviceRepository branchDeviceRepository, BranchFiscalModuleRepository branchFiscalModuleRepository, FiscalizationRepository fiscalizationRepository, EmployeeRepository employeeRepository, ValidationContext validationContext) {
        super(branchRepository);
        this.branchRepository = branchRepository;
        this.addressMapper = addressMapper;
        this.customerRepository = customerRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.branchFiscalModuleRepository = branchFiscalModuleRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.employeeRepository = employeeRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(BranchDTO branchDTO) {
        validationContext.validate(branchDTO);
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setParentId(branchDTO.getParentId());
        branch.setCompanyId(getCompanyId(branchDTO.getCompanyId()));
        branch.setAddress(addressMapper.toEntity(branchDTO.getAddress()));
        branchRepository.save(branch);
        return branch.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, BranchDTO branchDTO) {
        branchDTO.setId(id);
        validationContext.validate(branchDTO);
        return branchRepository.findById(branchDTO.getId()).map(branch -> {
            branch.setName(branchDTO.getName());
            if (Objects.nonNull(branchDTO.getParentId()) && !Objects.equals(branch.getParentId(), branchDTO.getParentId())) {
                branch.setParent(branchRepository.findById(branchDTO.getParentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH)));
            }
            branch.setParentId(branchDTO.getParentId());
            branch.setAddress(addressMapper.merge(branchDTO.getAddress(), Optional.ofNullable(branch.getAddress()).orElseGet(BaseAddress::new)));
            branchRepository.save(branch);
            return branch.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public BranchDetailAdminDTO get(Long id) {
        return branchRepository.findById(id).map(branch -> {
            BranchDetailAdminDTO branchDetailDTO = new BranchDetailAdminDTO();
            branchDetailDTO.setId(branch.getId());
            branchDetailDTO.setName(branch.getName());
            branchDetailDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
            branchDetailDTO.setCompany(branch.getCompany().toShortDTO());
            branchDetailDTO.setStatus(branch.getStatus());
            if (branch.getParentId() != null) {
                branchDetailDTO.setParent(branch.getParent().toCommonDTO());
            }
            return branchDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public Page<BranchListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Branch> resultList = branchRepository.getResultList(filter);
        List<BranchListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(branch -> {
                    BranchListAdminDTO branchListDTO = new BranchListAdminDTO();
                    branchListDTO.setId(branch.getId());
                    branchListDTO.setName(branch.getName());
                    branchListDTO.setAddress(addressMapper.toShortDTO(branch.getAddress()));
                    branchListDTO.setStatus(branch.getStatus());
                    if (branch.getParentId() != null) {
                        branchListDTO.setParent(branch.getParent().toCommonDTO());
                    }
                    branchListDTO.setCompany(branch.getCompany().toShortDTO());
                    return branchListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return branchRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        branchRepository
                .findById(id)
                .map(branch -> {
                    branch.setStatus(status);
                    branchRepository.save(branch);
                    return branch.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
    }

    @Override
    public List<CommonTreeDTO> treeList(BaseFilter filter) {
        return recursion(branchRepository.findAllByCompanyIdAndStatusAndDeletedIsFalse(filter.getCompanyId(), CommonStatus.ACTIVE));
    }

    private List<CommonTreeDTO> recursion(List<Branch> branches) {
        Map<Long, CommonTreeDTO> branchesMap = branches.stream()
                .collect(Collectors.toMap(Branch::getId, branch -> new CommonTreeDTO(branch.getId(), branch.getName())));

        List<CommonTreeDTO> result = new ArrayList<>();
        branches.forEach(branch -> {
            CommonTreeDTO branchTreeDTO = branchesMap.get(branch.getId());
            if (branch.getParentId() == null) {
                result.add(branchTreeDTO);
            } else {
                CommonTreeDTO parentBranchTreeDTO = branchesMap.get(branch.getParentId());
                if (parentBranchTreeDTO != null) {
                    parentBranchTreeDTO.getChildren().add(branchTreeDTO);
                }
            }
        });
        return result;
    }

    @Override
    public BranchStatisticsAdminDTO getStatistics(Long id) {
        BranchStatisticsAdminDTO branchStatisticsAdminDTO = new BranchStatisticsAdminDTO();
        branchStatisticsAdminDTO.setCustomerCount(customerRepository.countAllByBranchIdAndDeletedIsFalse(id));
        branchStatisticsAdminDTO.setCustomerBranchCount(customerBranchRepository.countAllByBranchIdAndDeletedIsFalse(id));
        branchStatisticsAdminDTO.setDeviceCount(branchDeviceRepository.countAllByBranchId(id));
        branchStatisticsAdminDTO.setFiscalModuleCount(branchFiscalModuleRepository.countAllByBranchId(id));
        branchStatisticsAdminDTO.setFiscalizationCount(fiscalizationRepository.countAllByBranchId(id));
        branchStatisticsAdminDTO.setEmployeeCount(employeeRepository.countAllByBranchIdAndDeletedIsFalse(id));
        return branchStatisticsAdminDTO;
    }
}
