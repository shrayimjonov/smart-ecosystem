package uz.uzkassa.cts.handlers.export.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.cases.CaseTypeAccess;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.DeregisterReason;
import uz.uzkassa.cts.enums.Reason;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.repository.CaseRepository;
import uz.uzkassa.cts.repository.CaseTypeAccessRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.DateUtil;

import java.lang.reflect.Field;
import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CaseExportExcelHandler extends AbstractExportExcelHandler<CaseFilter> {

    CaseRepository caseRepository;
    CaseTypeAccessRepository caseTypeAccessRepository;
    ObjectMapper objectMapper;

    @Override
    public String getFileName(CaseFilter filter) {
        return "Cases.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Тип изменения", "ККМ сер.н", "ККМ новый сер.н", "ФМ сер.н", "ФМ новый сер.н", "Причина", "Оператор", "Регион", "Статус", "Дата создания", "Дата последнего изменения", "Кейс ID", "Старый менеджер", "Новый менеджер");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        if (SecurityUtils.isCurrentUserInRole(Role.ADMIN.getRoleCode()) && !SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
            filter.setAdminCaseTypes(caseTypeAccessRepository.findByEmployeeId(SecurityUtils.getCurrentUserId()).stream().map(CaseTypeAccess::getCaseType).toList());
        }
        List<Cases> result = caseRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Cases cases : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(cases.getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(cases.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(cases.getCaseType().getName());
            row.createCell(cellIndex++).setCellValue(getClassFieldValue(cases, "deviceSerialNumbers"));
            row.createCell(cellIndex++).setCellValue(getClassFieldValue(cases, "newDeviceSerialNumber"));
            row.createCell(cellIndex++).setCellValue(getClassFieldValue(cases, "fmSerialNumber"));
            row.createCell(cellIndex++).setCellValue(getClassFieldValue(cases, "newFiscalModuleSerial"));
            row.createCell(cellIndex++).setCellValue(getReason(cases.getJsonProperties(), cases.getCaseType()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(cases.getCreator()).map(User::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(cases.getCreator()).map(User::getBranch).map(Branch::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(cases.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(cases.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(cases.getLastModifiedDate()));
            row.createCell(cellIndex++).setCellValue(cases.getId());
            row.createCell(cellIndex++).setCellValue(CaseType.CHANGE_MANAGER == cases.getCaseType() ? Optional.ofNullable(getFieldValue(cases, "oldManagerName")).map(Object::toString).orElse("") : "");
            row.createCell(cellIndex).setCellValue(CaseType.CHANGE_MANAGER == cases.getCaseType() ? Optional.ofNullable(getFieldValue(cases, "newManagerName")).map(Object::toString).orElse("") : "");
        }
        return workbook;
    }

    private String getClassFieldValue(Cases cases, String fieldName) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return Optional.ofNullable(f.get(obj)).map(Object::toString).orElse("");
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            if (e instanceof NoSuchFieldException && fieldName.equals("deviceSerialNumbers"))
                return getClassFieldValue(cases, "deviceSerialNumber");
            else if (e instanceof NoSuchFieldException && fieldName.equals("fmSerialNumber"))
                return getClassFieldValue(cases, "fiscalModuleSerial");
            else if (e instanceof NoSuchFieldException)
                return "";
            else
                throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(Cases cases, String fieldName) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getReason(String json, CaseType type) {
        Map<String, Object> reasonMap = convertToDto(json, HashMap.class);
        if (reasonMap != null && !reasonMap.isEmpty()) {
            if (Objects.nonNull(reasonMap.get("reason")) && StringUtils.isNotBlank(reasonMap.get("reason").toString())) {
                switch (type) {
                    case DEREGISTER_KKM -> {
                        String str1 = convertToString(reasonMap.get("reason"));
                        DeregisterReason deregisterReason = convertToDto(str1, DeregisterReason.class);
                        if (Objects.nonNull(deregisterReason)) {
                            return deregisterReason.getName();
                        }
                    }
                    case REPLACEMENT_FM -> {
                        String str2 = convertToString(reasonMap.get("reason"));
                        Reason reasonEnum = convertToDto(str2, Reason.class);
                        if (Objects.nonNull(reasonEnum)) {
                            return reasonEnum.getName();
                        }
                    }
                    default -> {
                        return reasonMap.get("reason").toString();
                    }
                }
            }
        }
        return "";
    }

    private <T> T convertToDto(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
