package uz.uzkassa.silen.domain.mongo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;

@ToString
@Getter
@Setter
//@CompoundIndexes(value = {
//    @CompoundIndex(name = "org_par_idx", def = "{'organizationId': 1, 'partyId': 1}")
//})
@Document(collection = "mark")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ValidationCode {
    @Id
    private String id;
    @Indexed
    String code;
    String printCode;
    @Indexed
    String partyId;
    String productId;
    @Indexed
    String parentCode;
    boolean used;
    @Enumerated(EnumType.STRING)
    PackageType unit;
    AggregationStatus status;
    String organizationId;
    String organizationName;
    boolean block;

    public boolean isBlock() {
        if (this.block) return true;
        return unit != null && unit == PackageType.BLOCK;
    }
}
