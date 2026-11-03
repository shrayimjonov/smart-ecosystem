package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Contract;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends BaseRepository<Contract, Long> {

    List<Contract> findByCustomerIdAndTemplateTypeEnum(Long customerId, ThymeleafTemplateType templateTypeEnum);

    Optional<Contract> findFirstByTemplateTypeEnumAndCustomer_IdOrderByIdDesc(ThymeleafTemplateType templateTypeEnum, Long customerId);

    Contract findByCustomerId(Long customerId);

}
