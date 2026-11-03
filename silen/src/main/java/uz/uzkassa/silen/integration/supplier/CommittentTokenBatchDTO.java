package uz.uzkassa.silen.integration.supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommittentTokenBatchDTO {

    List<CommittentTokenDTO> companies = new LinkedList<>();
}

