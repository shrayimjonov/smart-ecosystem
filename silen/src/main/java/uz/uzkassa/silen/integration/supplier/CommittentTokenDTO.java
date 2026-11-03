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
public class CommittentTokenDTO {

    String tin;

    String token;

    List<String> committents;

    public CommittentTokenDTO(String tin, String token, List<String> committents) {
        this.tin = tin;
        this.token = token;
        this.committents = new LinkedList<>(committents);
    }
}

