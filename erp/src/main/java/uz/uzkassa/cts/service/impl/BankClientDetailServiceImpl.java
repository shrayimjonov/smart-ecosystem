package uz.uzkassa.cts.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;
import uz.uzkassa.cts.mappings.BankClientDetailMapper;
import uz.uzkassa.cts.repository.BankClientDetailRepository;
import uz.uzkassa.cts.service.BankClientDetailService;
import uz.uzkassa.cts.service.LedgerService;

@Service
@Transactional
public class BankClientDetailServiceImpl extends BaseServiceImpl<BankClientDetail, Long> implements BankClientDetailService {

    private final LedgerService ledgerService;
    private final BankClientDetailRepository bankClientDetailRepository;
    private final BankClientDetailMapper bankClientDetailMapper;

    public BankClientDetailServiceImpl(BankClientDetailRepository bankClientDetailRepository, LedgerService ledgerService, BankClientDetailMapper bankClientDetailMapper) {
        super(bankClientDetailRepository);
        this.bankClientDetailRepository = bankClientDetailRepository;
        this.ledgerService = ledgerService;
        this.bankClientDetailMapper = bankClientDetailMapper;
    }

    @Override
    public BankClientDetailsDto approve(Long id) {
        BankClientDetail detail = findById(id);

        ledgerService.importFromJournalDetail(detail);

        detail.setApproved(true);
        bankClientDetailRepository.save(detail);

        return bankClientDetailMapper.toDto(detail);
    }
}
