package uz.uzkassa.smartpos.utils;

import org.junit.jupiter.api.Test;
import uz.uzkassa.smartpos.enums.ProductGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkingUtilsTest {

    @Test
    void clearWithProductGroup() {
//        Pharma
        String clearedCode = MarkingUtils.clearWithProductGroup("0105712249124472211441460931991", MarkingUtils.getGroupCode("20250124000754251026"));
        assertEquals("0105712249124472211441460931991", clearedCode);
//        Pharma
        clearedCode = MarkingUtils.clearWithProductGroup("010380071400122821S2KY6T7NY804W91UZF092QVc0bHZkwMvrSnmNlRMXetsPhLP9ECIjhUkjC0ATdLY=", null);
        assertEquals("010380071400122821S2KY6T7NY804W", clearedCode);
//        Alcohol block
        clearedCode = MarkingUtils.clearWithProductGroup("011478007681527421&rsKWCZZCSmfX93d1ZX", ProductGroup.Alcohol);
        assertEquals("011478007681527421&rsKWCZZCSmfX", clearedCode);
//        Water
        clearedCode = MarkingUtils.clearWithProductGroup("010486001900135321S*F4bPSq(000)", ProductGroup.Water);
        assertEquals("010486001900135321S*F4bPSq(000)", clearedCode);

////        Alcohol block
//        clearedCode = MarkingUtils.clearWithProductGroup("011478001074851421vHNio&U93qmLk", ProductGroup.Alcohol);
//        assertEquals("011478001074851421vHNio&U93qmLk", clearedCode);
//        Water
        clearedCode = MarkingUtils.clearWithProductGroup("0104780068430013214<NTNZOnTCbdo", ProductGroup.Water);
        assertEquals("0104780068430013214<NTNZOnTCbdo", clearedCode);
//        Alcohol
        clearedCode = MarkingUtils.clearWithProductGroup("010478008311132421Go%-\"N693ckty", ProductGroup.Alcohol);
        assertEquals("010478008311132421Go%-\"N6", clearedCode);
//        Alcohol
        clearedCode = MarkingUtils.clearWithProductGroup("010478008311132421Go%-\"N693ckty", null);
        assertEquals("010478008311132421Go%-\"N6", clearedCode);
    }

    @Test
    void clearParentheses() {
        var code = MarkingUtils.clearParentheses("(01)08801116005581(21)(W+oMJR");
        assertEquals("010880111600558121(W+oMJR", code);
    }
}