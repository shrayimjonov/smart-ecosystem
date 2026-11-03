package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Branch;
import uz.uzkassa.silen.dto.BranchDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.BranchFilter;
import uz.uzkassa.silen.enumeration.BranchStatus;
import uz.uzkassa.silen.repository.pg.BranchRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService extends BaseService {
    private final BranchRepository branchRepository;

    public String create(BranchDTO branchDTO) {
        return save(null, branchDTO);
    }

    public String update(String id, BranchDTO branchDTO) {
        return save(id, branchDTO);
    }

    public String save(String id, BranchDTO branchDTO) {
        Branch branchEntity = Optional.ofNullable(id).flatMap(branchRepository::findById).orElse(new Branch());
        branchEntity.setName(branchDTO.getName());
        branchEntity.setStatus(branchDTO.getStatus());
        branchEntity.setBranchCode(branchDTO.getBranchCode());
        branchEntity.setMain(branchDTO.isMain());
        branchEntity.setLat(branchDTO.getLat());
        branchEntity.setLon(branchDTO.getLon());
        branchEntity.setAddress(branchDTO.getAddress());
        branchEntity.setDistrictId(branchDTO.getDistrictId());
        branchEntity.setManagerId(branchDTO.getManagerId());
        branchEntity.setOrganizationId(Optional.ofNullable(branchDTO.getOrganizationId()).orElse(SecurityUtils.getCurrentOrganizationId()));
        branchRepository.save(branchEntity);
        return branchEntity.getId();
    }

    public Page<BranchDTO> findAllByFilter(BranchFilter filter) {
        return branchRepository.findAllByFilter(filter).map(this::toDTO);
    }

    private BranchDTO toDTO(Branch branch) {
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(branch.getId());
        branchDTO.setName(branch.getName());
        branchDTO.setStatus(branch.getStatus());
        branchDTO.setBranchCode(branch.getBranchCode());
        branchDTO.setMain(branch.isMain());
        branchDTO.setLat(branch.getLat());
        branchDTO.setLon(branch.getLon());
        branchDTO.setAddress(branch.getAddress());
        branchDTO.setOrganizationId(branchDTO.getOrganizationId());
        if (branch.getDistrictId() != null) {
            branchDTO.setDistrict(branch.getDistrict().toDto());
            branchDTO.setRegion(branch.getDistrict().getRegion().toDto());
        }
        branchDTO.setManagerId(branch.getManagerId());
        if (branch.getManager() != null) {
            branchDTO.setManager(branch.getManager().toDto());
        }

        return branchDTO;
    }

    public BranchDTO get(String id) {
        Branch branch = branchRepository.findById(id).orElseThrow(notFound("Branch not found"));
        return toDTO(branch);
    }

    public void delete(String id) {
        branchRepository.deleteById(id);
    }

    public String updateStatus(String id, BranchStatus branchStatus) {
        Branch branch = branchRepository.findById(id).orElseThrow(notFound("Branch not found"));
        if (branch.getStatus() == branchStatus) {
            throw badRequest("Branch status already " + branchStatus.getNameRu()).get();
        }
        branch.setStatus(branchStatus);
        branchRepository.save(branch);
        return branch.getId();
    }

    public List<SelectItem> getItems(BranchFilter filter) {
        return branchRepository.findAllByFilter(filter)
                .map(branch -> new SelectItem(branch.getId(), branch.getName()))
                .getContent();
    }
}
