package uz.uzkassa.smartpos.utils;

import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

import java.util.EnumSet;
import java.util.List;

public final class TestUtils {
    public static <T extends Enum<T>> T option(EnumSet<T> options) {
        if (options == null || options.isEmpty()) return null;
        var index = RandomUtils.nextInt(0, options.size());
        return options.stream().toList().get(index);
    }

    @SuppressWarnings("unused")
    public static <T> T option(List<T> options) {
        if (options == null || options.isEmpty()) return null;
        var index = RandomUtils.nextInt(0, options.size());
        return options.get(index);
    }
}
