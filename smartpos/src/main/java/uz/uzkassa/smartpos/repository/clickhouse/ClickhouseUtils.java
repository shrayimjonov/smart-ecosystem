package uz.uzkassa.smartpos.repository.clickhouse;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.format.DateTimeFormatter;

@Component
public class ClickhouseUtils {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void setLimits(StringBuilder query, Integer page, Integer size) {
        query.append(" LIMIT ").append(page * size).append(", ").append(size);
    }

    public static void setSortOrder(StringBuilder query, BaseFilter filter) {
        if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
            query.append(" ").append(SortTypeEnum.desc.name()).append(" ");
        }
    }
}
