package uz.uzkassa.smartpos.base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uz.uzkassa.smartpos.containers.CustomClickhouseContainer;


public class BaseClickhouseIT {

    @BeforeAll
    static void setUp() {
        clickhouse.start();
    }

    @AfterAll
    static void tearDown() {
        clickhouse.stop();
    }

    public static final CustomClickhouseContainer clickhouse = CustomClickhouseContainer.getInstance();

}
