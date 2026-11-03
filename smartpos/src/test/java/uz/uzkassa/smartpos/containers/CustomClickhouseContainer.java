package uz.uzkassa.smartpos.containers;

import org.testcontainers.clickhouse.ClickHouseContainer;

public class CustomClickhouseContainer extends ClickHouseContainer {
    public static final String IMAGE_VERSION = "clickhouse/clickhouse-server:24.8.4";
    private static CustomClickhouseContainer container;


    private CustomClickhouseContainer() {
        super(IMAGE_VERSION);
    }

    public static CustomClickhouseContainer getInstance() {
        if (container == null) {
            container = new CustomClickhouseContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.clickhouse.url", container.getJdbcUrl());
        System.setProperty("spring.clickhouse.username", container.getUsername());
        System.setProperty("spring.clickhouse.password", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
