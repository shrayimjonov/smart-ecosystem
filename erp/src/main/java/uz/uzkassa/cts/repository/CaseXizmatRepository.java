package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.repository.custom.CaseXizmatRepositoryCustom;

import java.util.Optional;

@Repository
public interface CaseXizmatRepository extends BaseRepository<CaseXizmat, Long>, CaseXizmatRepositoryCustom {

    Optional<CaseXizmat> findFirstByCaseTypeAndReasonAndStatusOrderByIdDesc(CaseType type, FMReportReason reason, CommonStatus status);

    Optional<CaseXizmat> findFirstByCaseTypeAndStatusOrderByIdDesc(CaseType type, CommonStatus status);

    Optional<CaseXizmat> findFirstByTypeAndStatusOrderByIdDesc(CaseXizmatType type, CommonStatus status);

    boolean existsByTypeAndStatus(CaseXizmatType type, CommonStatus status);

    boolean existsByCaseTypeAndStatus(CaseType type, CommonStatus status);

    boolean existsByCaseTypeAndReasonAndStatus(CaseType type, FMReportReason reason, CommonStatus status);
}
