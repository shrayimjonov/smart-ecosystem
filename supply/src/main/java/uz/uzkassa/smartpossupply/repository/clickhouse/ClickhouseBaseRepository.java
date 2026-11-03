package uz.uzkassa.smartpossupply.repository.clickhouse;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.interfaces.ClickhouseTable;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.utils.BeanUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.08.2023
 */

@Component
public abstract class ClickhouseBaseRepository implements ClickhouseTable {


    @Autowired
    @Qualifier("clickhouseJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    protected static Long getCompanyId(BaseFilter filter) {
        return getCompanyId(filter, true);
    }

    protected static Long getCompanyId(BaseFilter filter, boolean throwException) {
        Long companyId = Optional.ofNullable(filter.getCompanyId()).orElse(SecurityUtils.getCurrentCompanyId());
        if (companyId == null && throwException) {
            LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
            throw new BadRequestException(localizationService.localize(MessageConstants.BAD_REQUEST_FROM_CLIENT));
        }
        return companyId;
    }

    public void insert(String insertQuery) {
        jdbcTemplate.update("INSERT INTO " + getTableName() + " FORMAT JSONEachRow " + insertQuery);
    }

    protected String convertLongListToString(List<Long> ids) {
        StringBuilder idsString = new StringBuilder();
        ids.forEach(s -> idsString.append(s).append(","));
        if (idsString.toString().endsWith(",")) {
            idsString.replace(idsString.length() - 1, idsString.length(), "");
        }
        return idsString.toString();
    }
}
