package uz.uzkassa.smartpossupply.utils;

import java.util.Collection;
import java.util.Iterator;

public final class QueryUtils {

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
}
