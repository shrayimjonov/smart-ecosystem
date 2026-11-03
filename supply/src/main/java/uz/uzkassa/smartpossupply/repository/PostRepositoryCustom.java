package uz.uzkassa.smartpossupply.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.post.Post;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Repository
public interface PostRepositoryCustom {
    ResultList<Post> getResultList(BaseFilter filter);
}
