package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.domain.BankClientJournal;

public interface ImportToLedgerService {
    void importToLedger(BankClientDetailRejected rejected, BankClientJournal journal);
}
