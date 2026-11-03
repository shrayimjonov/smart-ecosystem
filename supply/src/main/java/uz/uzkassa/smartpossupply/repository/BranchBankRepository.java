package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import uz.uzkassa.smartpossupply.domain.branch.BranchBank;

import java.util.Optional;

public interface BranchBankRepository extends BaseRepository<BranchBank, Long> {

    @EntityGraph(attributePaths = {"bank", "branch", "bank.parent"})
    Optional<BranchBank> findByBranchId(Long branchId);
}
