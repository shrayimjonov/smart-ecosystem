package uz.uzkassa.cts.domain;

import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Month;

@Embeddable
public class RentPaymentHistoryId implements Serializable {

    @Serial
    private static final long serialVersionUID = 9846456562L;

    private Long customerId;
    private String deviceSerialNumber;
    private Month month;
    private Integer year;


    public RentPaymentHistoryId(Long customerId, String deviceSerialNumber, Month month, Integer year) {
        this.customerId = customerId;
        this.deviceSerialNumber = deviceSerialNumber;
        this.month = month;
        this.year = year;
    }

    public RentPaymentHistoryId() {

    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentPaymentHistoryId that = (RentPaymentHistoryId) o;

        if (!customerId.equals(that.customerId)) return false;
        if (!deviceSerialNumber.equals(that.deviceSerialNumber)) return false;
        if (month != that.month) return false;
        return year.equals(that.year);
    }

    @Override
    public int hashCode() {
        int result = customerId.hashCode();
        result = 31 * result + deviceSerialNumber.hashCode();
        result = 31 * result + month.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
