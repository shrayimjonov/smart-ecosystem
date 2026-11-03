package uz.uzkassa.horeca.dto.billing;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BillingCustomerDTO implements Serializable {

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
        if (!(o instanceof BillingCustomerDTO)) {
            return false;
        }

        return (inn != null && inn.equals(((BillingCustomerDTO) o).inn)) || (ctoId != null && ctoId.equals(((BillingCustomerDTO) o).ctoId));
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
