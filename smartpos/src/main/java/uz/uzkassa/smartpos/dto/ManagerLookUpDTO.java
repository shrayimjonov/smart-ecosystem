package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagerLookUpDTO {
    private Long id;
    private String login;
    private String name;
}
