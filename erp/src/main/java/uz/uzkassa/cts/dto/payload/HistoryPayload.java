package uz.uzkassa.cts.dto.payload;

import java.io.Serializable;

public class HistoryPayload implements Serializable {

    private String serialNumber;
    private boolean fm;
    private String action;
    private String modifier;
    private Long modifierId;
    private String fromWarehouse;
    private String toWarehouse;
    private String fromValue;
    private String toValue;

    public static class HistoryBuilder implements Serializable {

        public HistoryBuilder() {

        }

        private String serialNumber;
        private boolean fm;
        private String action;
        private String modifier;
        private Long modifierId;
        private String fromWarehouse;
        private String toWarehouse;
        private String fromValue;
        private String toValue;

        public HistoryBuilder(String serialNumber, boolean fm, String action, String modifier, Long modifierId) {
            this.serialNumber = serialNumber;
            this.fm = fm;
            this.action = action;
            this.modifier = modifier;
            this.modifierId = modifierId;
        }

        public HistoryBuilder setFromWarehouse(String fromWarehouse) {
            this.fromWarehouse = fromWarehouse;
            return this;
        }

        public HistoryBuilder setToWarehouse(String toWarehouse) {
            this.toWarehouse = toWarehouse;
            return this;
        }

        public HistoryBuilder setFromValue(String fromValue) {
            this.fromValue = fromValue;
            return this;
        }

        public HistoryBuilder setToValue(String toValue) {
            this.toValue = toValue;
            return this;
        }

        public HistoryPayload build() {
            return new HistoryPayload(this);
        }

    }

    public HistoryPayload() {
    }

    public HistoryPayload(HistoryBuilder builder) {
        this.serialNumber = builder.serialNumber;
        this.fm = builder.fm;
        this.action = builder.action;
        this.modifier = builder.modifier;
        this.modifierId = builder.modifierId;
        this.fromWarehouse = builder.fromWarehouse;
        this.toWarehouse = builder.toWarehouse;
        this.fromValue = builder.fromValue;
        this.toValue = builder.toValue;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public boolean isFm() {
        return fm;
    }

    public String getAction() {
        return action;
    }

    public String getModifier() {
        return modifier;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public String getFromWarehouse() {
        return fromWarehouse;
    }

    public String getToWarehouse() {
        return toWarehouse;
    }

    public String getFromValue() {
        return fromValue;
    }

    public String getToValue() {
        return toValue;
    }
}
