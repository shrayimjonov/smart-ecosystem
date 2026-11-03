package uz.uzkassa.smartpos.dto.applicationroute.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApplicationRouteResponseDto implements Serializable {
    private Long id;
    private String endPoint;
    private String menuName;
    private String annotationName;
    private Integer position;
    private Long parentId;
    private String parentEndPoint;
    private String parentMenuName;
    private String parentAnnotationName;
    private boolean section;
}
