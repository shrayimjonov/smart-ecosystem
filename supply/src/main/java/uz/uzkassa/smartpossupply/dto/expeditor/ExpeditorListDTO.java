package uz.uzkassa.smartpossupply.dto.expeditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorListDTO extends FullNameDTO {

    static ObjectMapper objectMapper = new ObjectMapper();

    Long id;

    List<CommonDTO> planRoutes;

    Long orderCount;

    Long completedOrderCount;

    Long incomingOrderProductCount;

    Long outgoingOrderProductCount;

    Long remainderOrderProductCount;

    public ExpeditorListDTO(Long id, String firstName, String lastName, String patronymic, String planRoutes, Long orderCount, Long completedOrderCount, Long incomingOrderProductCount, Long outgoingOrderProductCount, Long remainderOrderProductCount) {
        this.id = id;
        super.firstName = firstName;
        super.lastName = lastName;
        super.patronymic = patronymic;
        try {
            this.planRoutes = objectMapper.readValue(planRoutes, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            this.planRoutes = null;
        }
        this.orderCount = orderCount;
        this.completedOrderCount = completedOrderCount;
        this.incomingOrderProductCount = incomingOrderProductCount;
        this.outgoingOrderProductCount = outgoingOrderProductCount;
        this.remainderOrderProductCount = remainderOrderProductCount;
    }
}