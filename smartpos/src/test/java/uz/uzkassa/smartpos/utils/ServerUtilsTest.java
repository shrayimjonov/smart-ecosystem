package uz.uzkassa.smartpos.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
class ServerUtilsTest {


    @Test
    void partition() {
        var set = Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
        var result = ServerUtils.partition(set, 4);
        assertEquals(3, result.size());
        assertEquals(set.size(), result.stream().map(Set::size).reduce(0, Integer::sum));
    }
}