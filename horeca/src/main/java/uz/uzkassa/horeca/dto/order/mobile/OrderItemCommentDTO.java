package uz.uzkassa.horeca.dto.order.mobile;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class OrderItemCommentDTO implements Serializable {
    String comment;
}
