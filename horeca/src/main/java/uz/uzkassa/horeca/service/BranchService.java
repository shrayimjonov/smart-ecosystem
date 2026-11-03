package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.branch.BranchDTO;
import uz.uzkassa.horeca.dto.branch.admin.BranchDetailAdminDTO;
import uz.uzkassa.horeca.dto.branch.admin.BranchListAdminDTO;
import uz.uzkassa.horeca.dto.branch.cabinet.BranchDetailCabinetDTO;
import uz.uzkassa.horeca.dto.branch.cabinet.BranchListCabinetDTO;
import uz.uzkassa.horeca.dto.hallitem.BranchFeeStatisticsDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.CategoryRepository;
import uz.uzkassa.horeca.repository.HallItemRepositoryCustom;
import uz.uzkassa.horeca.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BranchService extends BaseService {

    HallItemRepositoryCustom hallItemRepository;
    CategoryRepository categoryRepository;
    ProductRepository productRepository;

    private void validate(BranchDTO branchDTO) {
        Long companyId = getCurrentCompanyId(branchDTO.getCompanyId());
        if (StringUtils.isEmpty(branchDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (branchDTO.getId() == null && branchRepository.existsByCompanyIdAndDeletedIsFalseAndNameIgnoreCase(companyId, branchDTO.getName().trim())) {
            throw conflictExceptionThrow("Филиал с таким названием " + branchDTO.getName() + " уже существует.").get();
        }
        if (branchDTO.getId() != null && branchRepository.existsByCompanyIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(companyId, branchDTO.getName().trim(), branchDTO.getId())) {
            throw conflictExceptionThrow("Филиал с таким названием " + branchDTO.getName() + " уже существует.").get();
        }
    }

    @Transactional
    public Long create(BranchDTO branchDTO) {
        this.validate(branchDTO);
        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        branch.setAddress(addressMapper.toEntity(branchDTO.getAddress()));
        branch.setCompanyId(getCurrentCompanyId(branchDTO.getCompanyId()));
        branch.setStatus(CommonStatus.ACTIVE);
        branch.setManagerId(branchDTO.getOwnerId());
        if (branchDTO.getCtsId() != null) {
            branch.setCtsId(branchDTO.getCtsId());
        }
        branchRepository.save(branch);
        return branch.getId();
    }

    public Page<BranchListAdminDTO> getListForAdmin(BaseFilter filter) {
        return branchRepository.getResultList(filter)
                .map(branch -> {
                    BranchListAdminDTO branchListDTO = new BranchListAdminDTO();
                    branchListDTO.setId(branch.getId());
                    branchListDTO.setName(branch.getName());
                    branchListDTO.setStatus(branch.getStatus());
                    branchListDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                    return branchListDTO;
                });
    }

    public BranchDetailAdminDTO getForAdmin(Long id) {
        return branchRepository
                .findById(id)
                .map(branch -> {
                    BranchDetailAdminDTO branchDetailDTO = new BranchDetailAdminDTO();
                    branchDetailDTO.setId(branch.getId());
                    branchDetailDTO.setName(branch.getName());
                    branchDetailDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                    return branchDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, BranchDTO branchDTO) {
        return branchRepository
                .findById(id)
                .map(branch -> {
                    branchDTO.setId(id);
                    this.validate(branchDTO);
                    branch.setName(branchDTO.getName());
                    branch.setManagerId(branchDTO.getOwnerId());
                    branch.setAddress(
                            addressMapper.merge(branchDTO.getAddress(), Optional.ofNullable(branch.getAddress()).orElse(new Address()))
                    );
                    branchRepository.save(branch);
                    return branch.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    public Page<BranchListCabinetDTO> getListForCabinet(BaseFilter filter) {
        Map<Long, List<BranchFeeStatisticsDTO>> statsByBranch = hallItemRepository.getFeeByBranch(filter.getCompanyId())
                .stream()
                .collect(Collectors.groupingBy(BranchFeeStatisticsDTO::getBranchId));

        return branchRepository.getResultList(filter)
                .map(branch -> {
                    BranchListCabinetDTO branchListDTO = new BranchListCabinetDTO();
                    branchListDTO.setId(branch.getId());
                    branchListDTO.setName(branch.getName());
                    branchListDTO.setStatus(branch.getStatus());
                    branchListDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                    categoryRepository.findFirstByBranchIdAndCategoryTypeAndDeletedIsFalse(branch.getId(), CategoryType.CATERING_SERVICE)
                            .flatMap(category -> productRepository.findFirstByBranchIdAndCategoryIdAndDeletedIsFalse(branch.getId(), category.getId()))
                            .ifPresent(product -> branchListDTO.setVat(product.getVat()));
                    if (statsByBranch.containsKey(branch.getId())) {
                        BranchFeeStatisticsDTO branchFeeStatisticsDTOS = statsByBranch.get(branch.getId()).getFirst();
                        branchListDTO.setFeeMin(branchFeeStatisticsDTOS.getMinFee());
                        branchListDTO.setFeeMax(branchFeeStatisticsDTOS.getMaxFee());
                    }
                    if (branch.getManagerId() != null) {
                        branchListDTO.setOwner(branch.getManager().toCommonDTO());
                    }
                    return branchListDTO;
                });
    }

    public BranchDetailCabinetDTO getForCabinet(Long id) {
        return branchRepository
                .findById(id)
                .map(branch -> {
                    BranchDetailCabinetDTO branchDetailDTO = new BranchDetailCabinetDTO();
                    branchDetailDTO.setId(branch.getId());
                    branchDetailDTO.setName(branch.getName());
                    if (branch.getManagerId() != null) {
                        branchDetailDTO.setManager(branch.getManager().toCommonDTO());
                    }
                    branchDetailDTO.setAddress(addressMapper.toDetailDTO(branch.getAddress()));
                    return branchDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void delete(Long id) {
        branchRepository.deleteById(id);
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return branchRepository.getLookup(filter);
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        branchRepository
                .findById(id)
                .map(branch -> {
                    branch.setStatus(status);
                    branchRepository.save(branch);
                    return branch.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    public Long getActiveBranches(String companyInn) {
        Company company = companyRepository.findFirstByTin(companyInn).orElseThrow(notFoundExceptionThrow(Company.class.getName(), companyInn));
        return branchRepository.countAllByCompanyIdAndStatus(company.getId(), CommonStatus.ACTIVE);
    }
}
