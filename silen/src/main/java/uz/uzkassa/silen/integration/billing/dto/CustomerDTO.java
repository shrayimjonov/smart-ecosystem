package uz.uzkassa.silen.integration.billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO
 */
@Getter
@Setter
public class CustomerDTO  implements Serializable {

    @NotNull
    private String name;

    @NotNull
    private String inn;

    @NotNull
    private Long ctoId;

    private Integer terminalCount;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerDTO)) {
            return false;
        }

        return (inn != null && inn.equals(((CustomerDTO) o).inn)) ||  (ctoId != null && ctoId.equals(((CustomerDTO) o).ctoId));
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDTO{" +
            ", name='" + getName() + "'" +
            ", inn='" + getInn() + "'" +
            "}";
    }
}
