package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.WaybillProductGroups;

public interface WaybillProductGroupRepository extends JpaRepository<WaybillProductGroups, String> {

}
