package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.branch.BranchBank;
import uz.uzkassa.smartpossupply.dto.bankbranch.BranchBankDTO;
import uz.uzkassa.smartpossupply.dto.bankbranch.BranchBankDetailDTO;
import uz.uzkassa.smartpossupply.repository.BranchBankRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchBankService {

    BranchBankRepository branchBankRepository;
    ValidationService validationService;

    public BranchBankDetailDTO getByBranchId(Long bankId) {
        BranchBank branchBank = branchBankRepository.findByBranchId(bankId).orElseThrow(validationService.entityNotFoundExceptionThrow(BranchBank.class.getName(), "id", bankId));

        BranchBankDetailDTO branchBankDetailDTO = new BranchBankDetailDTO();
        branchBankDetailDTO.setId(branchBank.getId());
        branchBankDetailDTO.setAccountNumber(branchBank.getAccountNumber());
        branchBankDetailDTO.setOked(branchBank.getOked());
        branchBankDetailDTO.setMain(Boolean.FALSE);
        branchBankDetailDTO.setBank(branchBank.getBank().toLookUpDTO());
        if (branchBank.getBank().getParent() != null) {
            branchBankDetailDTO.setParentBank(branchBank.getBank().getParent().toLookUpDTO());
        }
        branchBankDetailDTO.setBranch(branchBank.getBranch().toCommonDTO());
        return branchBankDetailDTO;
    }

    @Transactional
    public Long createOrUpdate(BranchBankDTO branchBankDTO) {
        BranchBank branchBank = branchBankRepository.findByBranchId(branchBankDTO.getBranchId()).orElse(new BranchBank());
        branchBank.setBranchId(branchBankDTO.getBranchId());
        branchBank.setBankId(branchBankDTO.getBankId());
        branchBank.setAccountNumber(branchBankDTO.getAccountNumber());
        branchBank.setOked(branchBankDTO.getOked());
        return branchBankRepository.save(branchBank).getId();
    }
}
