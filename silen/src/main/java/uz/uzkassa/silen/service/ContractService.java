package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Contract;
import uz.uzkassa.silen.dto.ContractDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ContractFilter;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.repository.pg.ContractRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService extends BaseService {
    private final ContractRepository contractRepository;

    @Transactional
    public ContractDTO save(ContractDTO contractDTO) {
        final String organizationId = Optional.ofNullable(getCurrentOrganizationId()).orElse(contractDTO.getOrganizationId());

        if (contractDTO.getId() != null) {
            contractRepository.findFirstByIdNotAndOrganizationIdAndNumberAndCustomerIdAndDateAndDeletedFalse(contractDTO.getId(), organizationId, contractDTO.getNumber(), contractDTO.getCustomerId(), contractDTO.getDate())
                .ifPresent(alreadyExists -> {
                    throw badRequest("Договор с таким номером уже существует").get();
                });
        } else {
            contractRepository.findFirstByOrganizationIdAndCustomerIdAndNumberAndDateAndDeletedFalse(organizationId, contractDTO.getCustomerId(),contractDTO.getNumber(),  contractDTO.getDate())
                .ifPresent(contract -> {
                    throw badRequest("Договор с таким номером уже существует").get();
                });
        }

        Contract contract = Optional.ofNullable(contractDTO.getId()).flatMap(contractRepository::findById).orElse(new Contract());
        contract.setCustomerId(contractDTO.getCustomerId());
        contract.setDate(contractDTO.getDate());
        contract.setNumber(contractDTO.getNumber());
        contract.setNote(contractDTO.getNote());
        contract.setStatus(contractDTO.getStatus());
        contract.setOrganizationId(organizationId);
        contractRepository.save(contract);
        return contract.toDto();
    }

    public Page<ContractDTO> findAll(ContractFilter filter) {
        return contractRepository.findAllByFilter(filter).map(Contract::toDto);
    }

    public List<SelectItem> getItems(ContractFilter filter) {
        return contractRepository.findAllByFilter(filter)
            .map(Contract::toSelectItem).getContent();
    }

    public ContractDTO findOne(String id) {
        return contractRepository.findById(id).map(Contract::toDto).orElseThrow(notFound("Договор с таким номером не найден"));
    }

    @Transactional
    public void changeStatus(String id, Status status) {
        Contract contract = contractRepository.findById(id).orElseThrow(notFound("Договор с таким номером не найден"));
        contract.setStatus(status);
        contractRepository.save(contract);
    }

    @Transactional
    public void delete(String id) {
        Contract contract = contractRepository.findById(id).orElseThrow(notFound("Договор с таким номером не найден"));
        contractRepository.delete(contract);
    }
}
