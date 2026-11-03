package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.address.BaseAddress_;
import uz.uzkassa.cts.dto.devicesuspendhistory.response.DeviceSuspendHistoryResponseDto;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;
import uz.uzkassa.cts.enums.PeriodType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.mappings.CustomerBranchDeviceMapper;
import uz.uzkassa.cts.mappings.DeviceSuspendHistoryMapper;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.DeviceSuspendHistoryRepository;
import uz.uzkassa.cts.service.DeviceSuspendHistoryOldService;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class DeviceSuspendHistoryOldServiceImpl extends BaseServiceImpl<DeviceSuspendHistory, Long> implements DeviceSuspendHistoryOldService {

    private final DeviceSuspendHistoryRepository repository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerBranchDeviceMapper customerBranchDeviceMapper;
    private final DeviceSuspendHistoryMapper deviceSuspendHistoryMapper;

    public DeviceSuspendHistoryOldServiceImpl(DeviceSuspendHistoryRepository repository,
                                              CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                              CustomerBranchDeviceMapper customerBranchDeviceMapper,
                                              DeviceSuspendHistoryMapper deviceSuspendHistoryMapper) {
        super(repository);
        this.repository = repository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerBranchDeviceMapper = customerBranchDeviceMapper;
        this.deviceSuspendHistoryMapper = deviceSuspendHistoryMapper;
    }

    @Override
    public DeviceSuspendHistory getLastByCustomerBranchDeviceId(Long customerBranchDeviceId) {
        Optional<DeviceSuspendHistory> history = repository.findFirstByCustomerBranchDeviceIdOrderByIdDesc(customerBranchDeviceId);
        return history.orElse(null);
    }

    @Override
    public Page<DeviceSuspendHistoryResponseDto> getByContractId(Long contractId, BaseFilter filter) {

        return repository.findByContractIdOrderByIdDesc(contractId, filter.getOrderedPageable())
                .map(deviceSuspendHistoryMapper::toDto);
    }


    @Override
    @Transactional
    public void suspend(Long deviceId) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByDeviceIdOrderByIdDesc(deviceId).orElse(null);
        if (customerBranchDevice == null) {
            return;
        }
        customerBranchDevice.setSuspendStatus(DeviceSuspendStatus.SUSPENDED);
        // suspend date for blocking device
        LocalDate suspendDate = LocalDate.now();
        if (PeriodType.YEARLY.equals(customerBranchDevice.getTariff().getPeriodType()))
            customerBranchDevice.setSuspendDate(suspendDate);
        else {
            suspendDate = customerBranchDevice.getDueDate();
            customerBranchDevice.setSuspendDate(suspendDate);
        }
        customerBranchDeviceRepository.save(customerBranchDevice);

        DeviceSuspendHistory history = new DeviceSuspendHistory();
        history.setActive(true);
        history.setCustomerBranchDeviceId(customerBranchDevice.getId());
        history.setCustomerBranchId(customerBranchDevice.getCustomerBranchId());
        history.setDeviceSerialNumber(customerBranchDevice.getDevice().getSerialNumber());
        history.setTariffId(customerBranchDevice.getTariffId());
        history.setContractId(customerBranchDevice.getContractId());
        history.setMonth(suspendDate.getMonth());
        history.setStartDate(suspendDate);
        history.setYear(suspendDate.getYear());

        repository.save(history);

        customerBranchDeviceMapper.toLstDto(customerBranchDevice);
    }

    @Override
    public Page<DeviceSuspendHistoryResponseDto> getList(DeviceSuspendHistoryFilter filter) {
        Specification<DeviceSuspendHistory> specification = getDeviceSuspendHistorySpecification(filter);
        return repository
                .findAll(specification, filter.getOrderedPageable())
                .map(deviceSuspendHistoryMapper::toDto);
    }

    private static Specification<DeviceSuspendHistory> getDeviceSuspendHistorySpecification(DeviceSuspendHistoryFilter filter) {
        return (root, criteriaQuery, cb) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Join<DeviceSuspendHistory, CustomerBranch> customerBranchJoin = root.join(DeviceSuspendHistory_.CUSTOMER_BRANCH);
            Join<DeviceSuspendHistory, Customer> customerJoin = root.join(DeviceSuspendHistory_.CONTRACT).join(Contract_.CUSTOMER);
            Path<BaseAddress> addressPath =  customerBranchJoin.get(CustomerBranch_.ADDRESS);
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customerJoin.get(Customer_.TIN);
                Path<String> oldTin = customerJoin.get(Customer_.OLD_TIN);
                Path<String> deviceNumberPath = root.get(DeviceSuspendHistory_.DEVICE_SERIAL_NUMBER);

                Predicate tinPred = cb.like(cb.lower(tin), filter.getSearchForQuery());
                Predicate oldTinPred = cb.like(cb.lower(oldTin), filter.getSearchForQuery());
                Predicate deviceNumberPred = cb.like(cb.lower(deviceNumberPath), filter.getSearchForQuery());

                list.add(cb.or(oldTinPred, tinPred, deviceNumberPred));
            }

            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(DeviceSuspendHistory_.CREATED_DATE);
                list.add(cb.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.isBlockedBySystem()) {
                Path<LocalDate> dueDate = root.get(DeviceSuspendHistory_.DUE_DATE);
                list.add(cb.isNull(dueDate));
                Path<Long> creatorId = root.get(DeviceSuspendHistory_.CREATOR_ID);
                list.add(cb.isNull(creatorId));
            }
            if (filter.isCurrentBlocked()) {
                Path<LocalDate> dueDate = root.get(DeviceSuspendHistory_.DUE_DATE);
                list.add(cb.isNull(dueDate));
            }
            if (filter.getBranchId() != null) {
                list.add(cb.equal(customerJoin.get(Customer_.BRANCH_ID), filter.getBranchId()));
            }
            if (filter.getRegionId() != null) {
                list.add(cb.equal(addressPath.get(BaseAddress_.REGION_ID), filter.getRegionId()));
            }
            if (filter.getDistrictId() != null) {
                list.add(cb.equal(addressPath.get(BaseAddress_.DISTRICT_ID), filter.getDistrictId()));
            }

            return cb.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public byte[] download(DeviceSuspendHistoryFilter filter) {
        Page<DeviceSuspendHistory> suspendHistories =
                repository.findAll(getDeviceSuspendHistorySpecification(filter), filter.getOrderedPageable());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("DeviceSuspendHistory");
            String[] headerNames = new String[]{"id", "Tariff", "серийный номер устройства", "Название компании",
                    "StartDate", "DueDate", "month", "year", "creator", "modifier"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createDeviceSuspendHistory(suspendHistories, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while creating excel file", e);
        }
    }
}
