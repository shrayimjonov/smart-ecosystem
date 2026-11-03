package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Bill;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.08.2024
 */
public interface BillRepository extends BaseRepository<Bill, Long> {

    Optional<Bill> findFirstByBillId(String billId);
}
