package uz.uzkassa.cts.dto.rentact.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentActAccessoryRequestDto {
    @NotNull
    private Long Id;
    @NotNull
    @NotEmpty
    private String deviceSerialNumber;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentActAccessoryRequestDto that = (RentActAccessoryRequestDto) o;

        if (!Id.equals(that.Id)) return false;
        return  deviceSerialNumber.equals(that.deviceSerialNumber);
    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + deviceSerialNumber.hashCode();
        return result;
    }
}
