package uz.uzkassa.cts.dto.cases.caseproperty;


import uz.uzkassa.cts.constants.MessageConstants;

import java.util.List;

public interface CaseAction extends MessageConstants {
    void accept();

    List<String> generateTemplate();

    boolean is2Step();

}
