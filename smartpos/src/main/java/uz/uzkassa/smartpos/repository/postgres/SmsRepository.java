package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.Sms;

@Repository
public interface SmsRepository extends JpaRepository<Sms, Long>, JpaSpecificationExecutor<Sms> {
}
