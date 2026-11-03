package uz.uzkassa.cts.numbering;

import uz.uzkassa.cts.constants.Constants;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNumbering implements NumberingStrategy, Constants {

    protected abstract Long getIncrementNumber(Long companyId);

    @Override
    public Map<String, String> generateNumber(Long companyId) {
        Long incrementNumber = getIncrementNumber(companyId) + 1L;

        String number = getPrefix().getName()
                .concat(DateUtil.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        Map<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, number);
        return numberMap;
    }

}
