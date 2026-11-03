package uz.uzkassa.smartpos.dto.applicationroute.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ApplicationRouteRequestDto implements Serializable {

    Long id;

    String endPoint;

    String menuName;

    String annotationName;

    Integer position;

    Long parentId;

    boolean section;
}
