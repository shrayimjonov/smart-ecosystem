package uz.uzkassa.horeca.interfaces;

import java.util.HashMap;

public interface Numbering {
    HashMap<String, String> getNumberingMap(Long branchId);
}
