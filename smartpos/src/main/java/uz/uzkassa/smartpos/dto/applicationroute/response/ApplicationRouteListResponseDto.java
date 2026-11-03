package uz.uzkassa.smartpos.dto.applicationroute.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ApplicationRouteListResponseDto implements Serializable {
    private Long id;
    private String endPoint;
    private String menuName;
    private String annotationName;
    private Integer position;
    private Long parentId;
    private boolean section;
    private List<ApplicationRouteListResponseDto> subordinates;
}
