package uz.uzkassa.cts.handlers.export.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
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
public class CaseExportCsvHandler extends AbstractExportCsvHandler<CaseFilter> {

    CaseRepository caseRepository;
    CaseTypeAccessRepository caseTypeAccessRepository;
    ObjectMapper objectMapper;

    @Override
    public String getFileName(CaseFilter filter) {
        return "Cases.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Тип изменения", "ККМ сер.н", "ККМ новый сер.н", "ФМ сер.н", "ФМ новый сер.н", "Причина", "Оператор", "Регион", "Статус", "Дата создания", "Дата последнего изменения", "Кейс ID", "Старый менеджер", "Новый менеджер");
    }

    @Override
    protected void drawItemList(CaseFilter filter, CSVWriter csvWriter) {
        if (SecurityUtils.isCurrentUserInRole(Role.ADMIN.getRoleCode()) && !SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
            filter.setAdminCaseTypes(caseTypeAccessRepository.findByEmployeeId(SecurityUtils.getCurrentUserId()).stream().map(CaseTypeAccess::getCaseType).toList());
        }
        caseRepository
                .getResultList(filter)
                .getList()
                .forEach(cases ->
                        csvWriter.writeNext(
                                new String[]{
                                        cases.getCustomer().getTin(),
                                        cases.getCustomer().getName(),
                                        cases.getCaseType().getName(),
                                        getClassFieldValue(cases, "deviceSerialNumbers"),
                                        getClassFieldValue(cases, "newDeviceSerialNumber"),
                                        getClassFieldValue(cases, "fmSerialNumber"),
                                        getClassFieldValue(cases, "newFiscalModuleSerial"),
                                        getReason(cases.getJsonProperties(), cases.getCaseType()),
                                        Optional.ofNullable(cases.getCreator()).map(User::getName).orElse(""),
                                        Optional.ofNullable(cases.getCreator()).map(User::getBranch).map(Branch::getName).orElse(""),
                                        cases.getStatus().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(cases.getCreatedDate()),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(cases.getLastModifiedDate()),
                                        cases.getId().toString(),
                                        CaseType.CHANGE_MANAGER == cases.getCaseType() ? Optional.ofNullable(getFieldValue(cases, "oldManagerName")).map(Object::toString).orElse("") : "",
                                        CaseType.CHANGE_MANAGER == cases.getCaseType() ? Optional.ofNullable(getFieldValue(cases, "newManagerName")).map(Object::toString).orElse("") : ""
                                },
                                false
                        )
                );
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
