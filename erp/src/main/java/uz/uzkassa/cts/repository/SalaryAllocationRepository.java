package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.SalaryAllocation;
import uz.uzkassa.cts.repository.custom.SalaryAllocationRepositoryCustom;

@Repository
public interface SalaryAllocationRepository extends BaseRepository<SalaryAllocation, Long>, SalaryAllocationRepositoryCustom {
}
