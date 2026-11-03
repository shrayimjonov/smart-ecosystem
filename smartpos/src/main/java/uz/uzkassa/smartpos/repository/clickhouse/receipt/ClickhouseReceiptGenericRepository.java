package uz.uzkassa.smartpos.repository.clickhouse.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.filter.receipt.CreditAdvanceReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.integration.CheckReturnedReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.CreditAdvanceReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.CreditPaymentDTO;
import uz.uzkassa.smartpos.dto.receipt.LastReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.scheduling.CHCreditDto;
import uz.uzkassa.smartpos.dto.scheduling.SchedulePaymentDto;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClickhouseReceiptGenericRepository {
    Page<ReceiptDTO> findAllByFilter(ReceiptFilter filter, boolean forExcel);

    Optional<ReceiptDTO> findFirstByOriginUid(String originUid);

    List<ReceiptDTO> findAllByOriginUid(String originUid);

    Optional<ReceiptDTO> findFirstByFiscalUrl(String fiscalUrl);

    Optional<ReceiptDTO> findById(Long id, boolean noFiscal);

    ShiftReportDTO xReport(Long branchId, Long shiftNumber);

    ShiftReportDTO totalReport(Long branchId);

    Integer receiptNumberInShift(Long branchId, Long shiftNumber);

    Integer maxShiftNumber(Long branchId);

    Page<CreditAdvanceReceiptDTO> findAllCreditAdvanceReceipts(CreditAdvanceReceiptFilter filter);

    List<CreditPaymentDTO> findAllCreditPayments(String originUid);

    List<SchedulePaymentDto> findAllCreditPayments(Long id);

    Integer getAdvanceOrderFromOriginUid(String originUid);

    Optional<Pair<ReceiptStatus, String>> findUidByFiscalUrl(String fiscalUrl);

    Optional<CHCreditDto> findAdditionalByOriginUid(String originUid);

    Optional<CheckReturnedReceiptDto> checkReceiptByOriginUid(String uid, List<ReceiptStatus> statuses);

    Optional<LastReceiptDto> getLastReceipt(String serialNumber, String companyInn, PaymentType paymentType);
}
