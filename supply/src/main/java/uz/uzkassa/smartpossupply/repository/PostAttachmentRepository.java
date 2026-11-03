package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.post.PostAttachment;

import java.util.List;


@Repository
public interface PostAttachmentRepository extends BaseRepository<PostAttachment, Long> {

    @Modifying
    void deleteAllByPostId(Long postId);

    @EntityGraph(attributePaths = {"photo"})
    List<PostAttachment> findAllByPostIdOrderBySorder(Long postId);
}
