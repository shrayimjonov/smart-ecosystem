package uz.uzkassa.smartpossupply.numbering;


import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNumbering implements NumberingStrategy, Constants {

    private static final DecimalFormat COMMON_NUMBERING_FORMAT = new DecimalFormat("000000");

    protected abstract Long getIncrementNumber(Long companyId);

    @Override
    public Map<String, String> generateNumber(Long companyId) {
        Long incrementNumber = getIncrementNumber(companyId) + 1L;

        String number = getPrefix().getName()
            .concat(DateUtils.formatYearLastTwoDigit(LocalDate.now()))
            .concat("-")
            .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        Map<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, number);
        return numberMap;
    }

}
