package uz.uzkassa.smartpossupply.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.post.Post;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Repository
public interface PostRepository extends BaseRepository<Post, Long>, PostRepositoryCustom {
}
