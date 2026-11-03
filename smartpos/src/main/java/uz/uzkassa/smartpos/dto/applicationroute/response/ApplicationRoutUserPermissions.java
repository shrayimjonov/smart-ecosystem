package uz.uzkassa.smartpos.dto.applicationroute.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ApplicationRoutUserPermissions implements Serializable {
    private Long id;
    private String endPoint;
    private String menuName;
    private String annotationName;
    private Integer position;
    private Long parentId;
    private List<ApplicationRoutUserPermissions> children;
    private boolean allowed = false;
    private boolean section;
}
