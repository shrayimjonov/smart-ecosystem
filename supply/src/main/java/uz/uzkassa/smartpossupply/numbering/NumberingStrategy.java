package uz.uzkassa.smartpossupply.numbering;


import uz.uzkassa.smartpossupply.enums.Prefix;

import java.util.Map;

public interface NumberingStrategy {

    Prefix getPrefix(); // Identify the strategy by Prefix enum

    Map<String, String> generateNumber(Long companyId);

}
