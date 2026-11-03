package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Comment;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
@Repository
public interface CommentRepository extends BaseRepository<Comment, Long>, CommentRepositoryCustom {

}
