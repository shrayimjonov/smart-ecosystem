package uz.uzkassa.smartpos.integration.supply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateCommentDTO implements Serializable {
    @NotBlank
    String message;
}
