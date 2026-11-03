package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.shift.Shift;
import uz.uzkassa.smartpossupply.domain.shift.ShiftReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptBranchDTO;
import uz.uzkassa.smartpossupply.dto.shift.RegistrationReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ShiftReportStatisticDTO;
import uz.uzkassa.smartpossupply.dto.shift.XReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.XReportDetailDTO;
import uz.uzkassa.smartpossupply.dto.shift.ZReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ZReportListDTO;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;
import uz.uzkassa.smartpossupply.filters.ShiftReportFilter;
import uz.uzkassa.smartpossupply.repository.ShiftReportRepository;
import uz.uzkassa.smartpossupply.repository.ShiftRepository;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 17:37
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ShiftReportService extends BaseService {

    ShiftRepository shiftRepository;
    ShiftReportRepository shiftReportRepository;

    public XReportDetailDTO xReport() {
        Long branchId = getWebKassaBranchId();

        Shift shift = shiftRepository
            .findFirstByBranchIdAndStatusOrderByCreatedDateDesc(branchId, ShiftStatus.OPEN)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Shift.class.getSimpleName(), "branchId", branchId));

        XReportDTO xReport = shiftReportRepository.xReport(branchId, shift.getId());
        XReportDetailDTO shiftReportDTO = new XReportDetailDTO();
        shiftReportDTO.setReport(xReport);
        shiftReportDTO.setShift(shift.toCommonDTO());
        shiftReportDTO.setFiscalNumber(shift.getFiscalNumber());
        shiftReportDTO.setStartDate(shift.getCreatedDate());
        shiftReportDTO.setEndDate(shift.getEndDate());
        if (shift.getBranchId() != null) {
            shiftReportDTO.setBranch(new ReceiptBranchDTO(shift.getBranchId(),
                shift.getBranch().getName(),
                addressMapper.toDetailDTO(shift.getBranch().getAddress())
            ));
            shiftReportDTO.setCompany(shift.getBranch().getCompany().toCommonBaseDTO());
        }
        if (shift.getCashier() != null) {
            shiftReportDTO.setCashier(shift.getCashier().toCommonDTO());
        }
        shiftReportDTO.setStatistics(shiftReportRepository.getZReportStatistics(branchId, shift.getShiftNumber()));
        return shiftReportDTO;
    }

    public Page<ZReportListDTO> zReport(ShiftReportFilter filter) {
        filter.setBranchId(getWebKassaBranchId());
        ResultList<ShiftReport> resultList = shiftReportRepository.zReport(filter);
        List<ZReportListDTO> result = resultList
            .getList()
            .stream()
            .map(shiftReport -> {
                ZReportListDTO zReportListDTO = new ZReportListDTO();
                zReportListDTO.setId(shiftReport.getId());
                zReportListDTO.setShift(shiftReport.getShift().toCommonDTO());
                zReportListDTO.setTotalCash(shiftReport.getTotalCash());
                zReportListDTO.setTotalCard(shiftReport.getTotalCard());
                zReportListDTO.setTotalVat(shiftReport.getTotalVat());
                zReportListDTO.setStartDate(shiftReport.getStartDate());
                zReportListDTO.setEndDate(shiftReport.getEndDate());
                return zReportListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public ZReportDTO zReportDetails(Long branchId, Long shiftId) {
        ShiftReport shiftReport = shiftReportRepository.findFirstByBranchIdAndShiftId(branchId, shiftId).orElse(null);
        if (shiftReport == null) {
            throw validationService.notFoundExceptionThrow("Такой смены нет").get();
        }
        ZReportDTO zReportDTO = new ZReportDTO();
        zReportDTO.setId(shiftReport.getId());
        zReportDTO.setShift(shiftReport.getShift().toCommonDTO());
        zReportDTO.setFiscalNumber(shiftReport.getFiscalNumber());
        zReportDTO.setCompany(shiftReport.getBranch().getCompany().toCommonBaseDTO());
        zReportDTO.setBranch(new ReceiptBranchDTO(
            shiftReport.getBranchId(),
            shiftReport.getBranch().getName(),
            addressMapper.toDetailDTO(shiftReport.getBranch().getAddress())
        ));
        zReportDTO.setCashier(shiftReport.getCashier().toCommonDTO());
        zReportDTO.setStartDate(shiftReport.getStartDate());
        zReportDTO.setEndDate(shiftReport.getEndDate());

        zReportDTO.setTotalCash(shiftReport.getTotalCash());
        zReportDTO.setTotalCashReturned(shiftReport.getTotalCashReturned());
        zReportDTO.setTotalCard(shiftReport.getTotalCard());
        zReportDTO.setTotalCardReturned(shiftReport.getTotalCardReturned());
        zReportDTO.setTotalVat(shiftReport.getTotalVat());
        zReportDTO.setTotalVatReturned(shiftReport.getTotalVatReturned());
        zReportDTO.setReceiptCount(shiftReport.getReceiptCount());
        zReportDTO.setReceiptReturnedCount(shiftReport.getReceiptReturnedCount());

        zReportDTO.setStatistics(shiftReportRepository.getZReportStatistics(branchId, shiftReport.getShift().getShiftNumber()));
        return zReportDTO;
    }

    @Transactional
    public Long save(ShiftReportDTO shiftReportDTO) {
        ShiftReport shiftReport = new ShiftReport();
        shiftReport.setBranchId(getWebKassaBranchId());
        shiftReport.setShiftId(shiftReportDTO.getShiftId());
        shiftReport.setCashierId(shiftReportDTO.getCashierId());
        shiftReport.setFiscalNumber(shiftReportDTO.getFiscalNumber());
        shiftReport.setStartDate(shiftReportDTO.getStartDate());
        shiftReport.setEndDate(shiftReportDTO.getEndDate());
        shiftReport.setReceiptCount(shiftReportDTO.getReceiptCount());
        shiftReport.setReceiptReturnedCount(shiftReportDTO.getReceiptReturnedCount());
        shiftReport.setTotalCard(shiftReportDTO.getTotalCard());
        shiftReport.setTotalCardReturned(shiftReportDTO.getTotalCardReturned());
        shiftReport.setTotalCash(shiftReportDTO.getTotalCash());
        shiftReport.setTotalCashReturned(shiftReportDTO.getTotalCashReturned());
        shiftReport.setTotalVat(shiftReportDTO.getTotalVat());
        shiftReport.setTotalVatReturned(shiftReportDTO.getTotalVatReturned());
        shiftReportRepository.save(shiftReport);
        return shiftReport.getId();
    }

    public ShiftReportStatisticDTO getStatistics(ShiftReportFilter filter) {
        filter.setBranchId(getWebKassaBranchId());
        return shiftReportRepository.getStatistics(filter);
    }

    public RegistrationReportDTO getRegistrationReport() {
        Long branchId = getWebKassaBranchId();
        RegistrationReportDTO registrationReportDTO = shiftReportRepository.getRegistrationReport(branchId);
        branchRepository.findById(branchId)
            .ifPresent(branch -> {
                registrationReportDTO.setRegistrationDate(branch.getCreatedDate());
                registrationReportDTO.setSerialNumber(branch.getSerialNumber());
                registrationReportDTO.setBranch(new ReceiptBranchDTO(
                    branch.getId(),
                    branch.getName(),
                    addressMapper.toDetailDTO(branch.getAddress())
                ));
                registrationReportDTO.setCompany(branch.getCompany().toCommonBaseDTO());
            });
        return registrationReportDTO;
    }
}
