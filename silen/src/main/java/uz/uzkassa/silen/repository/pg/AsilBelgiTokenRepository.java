package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.AsilBelgiToken;

import java.time.LocalDateTime;
import java.util.List;

public interface AsilBelgiTokenRepository extends JpaRepository<AsilBelgiToken, String> {

    List<AsilBelgiToken> findAllByTokenDateBefore(LocalDateTime localDateTime);
}
