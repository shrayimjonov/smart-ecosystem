package uz.uzkassa.smartpos.dto.integration.referrallink;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ReferralLinkDTO implements Serializable {

    @NotNull
    @NotEmpty
    private String inn;

    @NotNull
    @NotEmpty
    private String promotionId;
}

