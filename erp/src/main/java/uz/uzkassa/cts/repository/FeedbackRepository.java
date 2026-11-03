package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.repository.custom.FeedbackRepositoryCustom;

@Repository
public interface FeedbackRepository extends BaseRepository<Feedback, Long>, FeedbackRepositoryCustom {
}
