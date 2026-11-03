package uz.uzkassa.smartpossupply.dto.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentListDTO implements Serializable {

    Long id;

    CommonDTO order;

    CommonDTO supplier;

    CommonDTO customer;

    String message;

    boolean fromSupplier;

    LocalDateTime commentDate;
}
