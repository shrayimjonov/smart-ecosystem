package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.ListResult;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.SelectItemLocalizedDTO;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatisticByStatusesDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.enums.CardTypeEnum;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.service.ReceiptService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/receipt")
@Slf4j
@RequiredArgsConstructor
public class ReceiptResource {
    private final ReceiptService receiptService;

    @PostMapping("/send")
    public ResponseData<String> sendReceipt(@RequestBody @Valid ReceiptDTO receiptDTO) {
        receiptDTO.setSource(Source.ONE_C);
        String uid = receiptService.sendReceipt(receiptDTO);
        return ResponseData.ok(uid);
    }

    @PostMapping("/batch")
    public void saveReceipts(@RequestBody @Valid List<ReceiptDTO> receiptList) {
        receiptService.saveReceiptList(receiptList);
    }

    @GetMapping(value = "/getByFiscalUrl")
    public ResponseEntity<ReceiptDTO> getByFiscalUrl(@RequestParam String fiscalUrl) {
        return ResponseEntity.ok(receiptService.getByFiscalUrl(fiscalUrl));
    }

    @GetMapping(value = "/return/getByFiscalUrl")
    public ResponseEntity<ReceiptDTO> getOriginByFiscalUrl(@RequestParam String fiscalUrl) {
        return ResponseEntity.ok(receiptService.getOriginByFiscalUrl(fiscalUrl));
    }

    @GetMapping(value = "/{uid}")
    public ResponseEntity<ReceiptDTO> getByUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(receiptService.getByUidMobile(uid));
    }

    @GetMapping(value = "/return/{uid}")
    public ResponseEntity<ReceiptDTO> getReceiptByOriginUid(@PathVariable("uid") String originUid) {
        return ResponseEntity.ok(receiptService.getByOriginUidMobile(originUid));
    }

    @DeleteMapping(value = "/deleteByUid/{uid}")
    public ResponseEntity<Void> deleteByUid(@PathVariable("uid") String uid) {
        receiptService.deleteByUid(uid);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        receiptService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Page<ReceiptDTO> getList(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED));
        return receiptService.getReceipts(receiptFilter, false);
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<ReceiptDTO>> getList2(@ParameterObject ReceiptFilter receiptFilter) {
        ListResult<ReceiptDTO> result = receiptService.getReceiptList(receiptFilter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/drafts")
    public List<ReceiptDTO> getDrafts() {
        return receiptService.findAllDrafts();
    }

    @GetMapping("/payment-types")
    public List<SelectItemLocalizedDTO> getPaymentTypes() {
        return PaymentType.getList();
    }

    @GetMapping("/card-types")
    public CardTypeEnum[] getCardTypes() {
        return CardTypeEnum.values();
    }

    @GetMapping("/operation")
    public Page<ReceiptDTO> getOperationList(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.INCOME, ReceiptStatus.WITHDRAW,
                ReceiptStatus.FLOW, ReceiptStatus.RETURN_FLOW, ReceiptStatus.INCASSATION));
        return receiptService.getReceipts(receiptFilter, false);
    }

    @GetMapping("/stat")
    public ReceiptStatDto getReceiptStat(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED));
        return receiptService.getReceiptStat(receiptFilter, false);
    }

    @GetMapping("/statistics/statuses")
    public ReceiptStatisticByStatusesDTO getReceiptStatisticByStatuses(@ParameterObject ReceiptFilter receiptFilter) {
        return receiptService.getReceiptStatisticByStatuses(receiptFilter, false);
    }

    @GetMapping("/statuses")
    public EnumSet<ReceiptStatus> getStatuses() {
        return EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.PAID_AND_NOT_FISCAL, ReceiptStatus.RETURNED, ReceiptStatus.ADVANCE, ReceiptStatus.CREDIT);
    }

    @GetMapping("/statuses/operation")
    public EnumSet<ReceiptStatus> getOperationStatuses() {
        return EnumSet.of(ReceiptStatus.INCOME, ReceiptStatus.WITHDRAW,
                ReceiptStatus.FLOW, ReceiptStatus.RETURN_FLOW, ReceiptStatus.INCASSATION);
    }

    @GetMapping("/source")
    public EnumSet<Source> getSource() {
        return EnumSet.allOf(Source.class);
    }

}
