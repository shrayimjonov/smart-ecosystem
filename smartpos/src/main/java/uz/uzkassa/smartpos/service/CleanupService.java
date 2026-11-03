package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CleanupService {
    private final ProductBranchService productBranchService;
    private final ReceiptService receiptService;
    private final StockAdjustmentService stockAdjustmentService;
    private final TransferService transferService;
    private final IncomeOfProductServiceV2 incomeOfProductService;
    private final RegisterService registerService;
    private final BalanceHistoryService balanceHistoryService;

    @Scheduled(cron = "10 10 16 * * *")
    public void cleanup() {
        Thread.startVirtualThread(stockAdjustmentService::cleanup);
        Thread.startVirtualThread(transferService::cleanup);
        Thread.startVirtualThread(incomeOfProductService::cleanup);
        Thread.startVirtualThread(registerService::cleanup);
        Thread.startVirtualThread(balanceHistoryService::cleanup);
        Thread.startVirtualThread(receiptService::cleanup);
        Thread.startVirtualThread(productBranchService::cleanup);
//        Thread.startVirtualThread(categoryService::cleanup); todo sharipov sunnatillo
    }
}
