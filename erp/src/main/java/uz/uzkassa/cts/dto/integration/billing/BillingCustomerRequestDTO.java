package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BillingCustomerRequestDTO implements Serializable {
    private Long id;
    private String name;
    private String inn;
    private Long ctoId;
    private String customerOKED;
    private String accountNumber;
    private String region;
    private String district;
    private String street;
    private String house;
    private String apartment;
    private String phone;

    private String pinfl;

    private List<String> appTypes;
    private String productGroup;
}

