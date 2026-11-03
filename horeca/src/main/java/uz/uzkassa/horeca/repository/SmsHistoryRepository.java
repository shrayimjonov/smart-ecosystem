package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.SmsHistory;

import java.util.Optional;

public interface SmsHistoryRepository extends BaseRepository<SmsHistory, String> {
    Optional<SmsHistory> findFirstByPhoneAndCodeOrderByCreatedDateDesc(String phone, String code);
    Optional<SmsHistory> findFirstByPhoneOrderByCreatedDateDesc(String phone);

    void deleteAllByPhone(String phone);
}
