package uz.uzkassa.cts.utils;

import uz.uzkassa.cts.filter.BaseFilter;

import java.util.Collection;
import java.util.Iterator;

public final class QueryUtils {


    public static StringBuilder getOffsetLimit(Integer page, Integer size) {
        StringBuilder sql = new StringBuilder();
        sql.append(" offset ").append(page * size).append(" limit ").append(size);
        return sql;
    }

    public static StringBuilder getOffsetLimit(BaseFilter filter) {
        return getOffsetLimit(filter.getPage(), filter.getSize());
    }


    public static String getSqlFormat(Collection<String> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            sb.append("'").append(iterator.next()).append("'");
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String getSqlLongFormat(Collection<Long> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<Long> iterator = list.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
