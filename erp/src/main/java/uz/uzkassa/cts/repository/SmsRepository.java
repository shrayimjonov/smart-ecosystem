package uz.uzkassa.cts.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Sms;

@Repository
public interface SmsRepository extends BaseRepository<Sms, Long> {
}
