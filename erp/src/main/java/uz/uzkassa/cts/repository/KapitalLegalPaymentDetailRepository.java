package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KapitalLegalPaymentDetailRepository extends BaseRepository<KapitalLegalPaymentDetail, Long> {
    List<KapitalLegalPaymentDetail> findAllBySentToBillingAndStateAndDirectionTypeAndIdInOrderByIdAsc(boolean isSent,
                                                                                                      KapitalLegalDocumentState state,
                                                                                                      KapitalLegalDirectionType directionType,
                                                                                                      List<Long> ids);

    List<KapitalLegalPaymentDetail> findAllBySentToBillingAndStateAndDirectionTypeAndDocumentTypeInOrderByIdAsc(boolean isSent,
                                                                                                                KapitalLegalDocumentState state,
                                                                                                                KapitalLegalDirectionType directionType,
                                                                                                                List<KapitalLegalDocumentType> documentTypes);

    List<KapitalLegalPaymentDetail> findByCreatedDateAfterAndPurposeContainingAndStateOrderByIdAsc(LocalDateTime createDate,
                                                                                                   String purpose,
                                                                                                   KapitalLegalDocumentState state);

    List<KapitalLegalPaymentDetail> findByIdGreaterThanAndPurposeContainingAndStateOrderByIdAsc(Long id,
                                                                                                String purpose,
                                                                                                KapitalLegalDocumentState state);
}
