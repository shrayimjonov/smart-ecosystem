package uz.uzkassa.smartpos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityTypeDTO {
    private Long id;
    private String name;
    private Long parentId;
}
