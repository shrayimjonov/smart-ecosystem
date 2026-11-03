package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.post.Post;
import uz.uzkassa.smartpossupply.domain.post.PostAttachment;
import uz.uzkassa.smartpossupply.domain.post.PostPhotoGallery;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.post.PostAttachmentDTO;
import uz.uzkassa.smartpossupply.dto.post.PostAttachmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.post.PostDTO;
import uz.uzkassa.smartpossupply.dto.post.PostDetailDTO;
import uz.uzkassa.smartpossupply.dto.post.PostListDTO;
import uz.uzkassa.smartpossupply.dto.post.PostPhotoGalleryDetailDTO;
import uz.uzkassa.smartpossupply.enums.AttachmentType;
import uz.uzkassa.smartpossupply.enums.PostStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.PostAttachmentRepository;
import uz.uzkassa.smartpossupply.repository.PostRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by:Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PostService extends BaseService {

    PostRepository postRepository;
    PostAttachmentRepository postAttachmentRepository;
    FileUploadService fileUploadService;

    @Transactional
    public Long create(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setPostDate(postDTO.getPostDate());
        post.setPostType(postDTO.getPostType());
        post.setPosition(postDTO.getPosition());
        if (isFromAdmin()) {
            post.setCompanyId(postDTO.getCompanyId());
        } else {
            post.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        postRepository.save(post);
        if (postDTO.getAttachments() != null) {
            int sorder = 0;
            for (PostAttachmentDTO postAttachmentDTO : postDTO.getAttachments()) {
                PostAttachment postAttachment = new PostAttachment();
                postAttachment.setPostId(post.getId());
                postAttachment.setSorder(sorder++);
                if (postAttachmentDTO.getText() != null) {
                    postAttachment.setAttachmentType(AttachmentType.TEXT);
                    postAttachment.setPostText(postAttachmentDTO.getText());
                } else if (postAttachmentDTO.getPhotoId() != null) {
                    postAttachment.setPhotoId(postAttachmentDTO.getPhotoId());
                    postAttachment.setAttachmentType(AttachmentType.PHOTO);
                } else if (
                    postAttachmentDTO.getPhotoGallery() != null && !postAttachmentDTO.getPhotoGallery().getPhotos().isEmpty()
                ) {
                    for (Long photoGalleryId : postAttachmentDTO.getPhotoGallery().getPhotos()) {
                        postAttachment.setAttachmentType(AttachmentType.PHOTO_GALLERY);
                        postAttachmentRepository.save(postAttachment);
                        PostPhotoGallery postPhotoGallery = new PostPhotoGallery();
                        postPhotoGallery.setPhotoId(photoGalleryId);
                        postPhotoGallery.setStructure(postAttachmentDTO.getPhotoGallery().getStructure());
                        postAttachment.addPhotoGallery(postPhotoGallery);
                    }
                }
                postAttachmentRepository.save(postAttachment);
            }
        }
        return post.getId();
    }

    @Transactional
    public Long update(Long id, PostDTO postDTO) {
        return postRepository
            .findById(id)
            .map(post -> {
                post.setTitle(postDTO.getTitle());
                post.setPostDate(postDTO.getPostDate());
                post.setPostType(postDTO.getPostType());
                post.setPosition(postDTO.getPosition());
                if (postDTO.getAttachments() != null) {
                    postAttachmentRepository.deleteAllByPostId(post.getId());
                    int sorder = 0;
                    for (PostAttachmentDTO postAttachmentDTO : postDTO.getAttachments()) {
                        PostAttachment postAttachment = new PostAttachment();
                        postAttachment.setPostId(post.getId());
                        postAttachment.setSorder(sorder++);
                        if (postAttachmentDTO.getText() != null) {
                            postAttachment.setAttachmentType(AttachmentType.TEXT);
                            postAttachment.setPostText(postAttachmentDTO.getText());
                        } else if (postAttachmentDTO.getPhotoId() != null) {
                            postAttachment.setPhotoId(postAttachmentDTO.getPhotoId());
                            postAttachment.setAttachmentType(AttachmentType.PHOTO);
                        } else if (
                            postAttachmentDTO.getPhotoGallery() != null && !postAttachmentDTO.getPhotoGallery().getPhotos().isEmpty()
                        ) {
                            for (Long photoGalleryId : postAttachmentDTO.getPhotoGallery().getPhotos()) {
                                postAttachment.setAttachmentType(AttachmentType.PHOTO_GALLERY);
                                postAttachmentRepository.save(postAttachment);
                                PostPhotoGallery postPhotoGallery = new PostPhotoGallery();
                                postPhotoGallery.setPhotoId(photoGalleryId);
                                postPhotoGallery.setStructure(postAttachmentDTO.getPhotoGallery().getStructure());
                                postAttachment.addPhotoGallery(postPhotoGallery);
                            }
                        }
                        postAttachmentRepository.save(postAttachment);
                    }
                }
                postRepository.save(post);
                return post.getId();
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
    }

    public PostDetailDTO get(Long id) {
        return postRepository
            .findById(id)
            .map(post -> {
                PostDetailDTO postDetailDTO = new PostDetailDTO();
                postDetailDTO.setId(post.getId());
                postDetailDTO.setTitle(post.getTitle());
                postDetailDTO.setPosition(post.getPosition());

                List<PostAttachment> postAttachments = postAttachmentRepository.findAllByPostIdOrderBySorder(id);
                for (PostAttachment postAttachment : postAttachments) {
                    PostAttachmentDetailDTO attachmentDetailDTO = new PostAttachmentDetailDTO();
                    attachmentDetailDTO.setAttachmentType(postAttachment.getAttachmentType());
                    if (postAttachment.getPostText() != null) {
                        attachmentDetailDTO.setText(postAttachment.getPostText());
                    } else if (postAttachment.getPhoto() != null) {
                        attachmentDetailDTO.setPhoto(postAttachment.getPhoto().toFileDTO());
                    } else if (postAttachment.getPhotoGalleries() != null && !postAttachment.getPhotoGalleries().isEmpty()) {
                        LinkedHashMap<Integer, ArrayList<FileDTO>> photoGalleriesMap = new LinkedHashMap<>();
                        for (PostPhotoGallery companyPhotoGallery : postAttachment.getPhotoGalleries()) {
                            ArrayList<FileDTO> photos = photoGalleriesMap.get(companyPhotoGallery.getStructure());
                            if (photos == null) {
                                photos = new ArrayList<>();
                            }
                            if (companyPhotoGallery.getPhoto() != null) {
                                photos.add(companyPhotoGallery.getPhoto().toFileDTO());
                                photoGalleriesMap.put(companyPhotoGallery.getStructure(), photos);
                            }
                        }
                        photoGalleriesMap.keySet().forEach(structureId -> {
                            PostPhotoGalleryDetailDTO postPhotoGalleryDetailDTO = new PostPhotoGalleryDetailDTO();
                            postPhotoGalleryDetailDTO.setStructure(structureId);
                            postPhotoGalleryDetailDTO.setPhotos(photoGalleriesMap.get(structureId));
                            attachmentDetailDTO.setPhotoGallery(postPhotoGalleryDetailDTO);
                        });
                    }
                    postDetailDTO.addAttachment(attachmentDetailDTO);
                }

                return postDetailDTO;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
    }

    public Page<PostListDTO> getList(BaseFilter filter) {
        ResultList<Post> resultList = postRepository.getResultList(filter);
        List<PostListDTO> result = resultList
            .getList()
            .stream()
            .map(post -> {
                PostListDTO postListDTO = new PostListDTO();
                postListDTO.setId(post.getId());
                postListDTO.setTitle(post.getTitle());
                postListDTO.setPostDate(post.getPostDate());
                postListDTO.setPosition(post.getPosition());
                return postListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        return postRepository.getResultList(filter).getList().stream().map(Post::toCommonDTO).toList();
    }

    @Transactional
    public void updateStatus(Long id, PostStatus status) {
        postRepository
            .findById(id)
            .map(post -> {
                post.setStatus(status);
                postRepository.save(post);
                return post;
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
    }

    @Transactional
    public List<Long> upload(List<MultipartFile> files) {
        return files.stream().map(file -> fileUploadService.fileUpload(file).getId()).toList();
    }
}
