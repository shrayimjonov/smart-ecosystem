package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.post.Post;
import uz.uzkassa.horeca.domain.post.PostAttachment;
import uz.uzkassa.horeca.domain.post.PostPhotoGallery;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.post.*;
import uz.uzkassa.horeca.enums.AttachmentType;
import uz.uzkassa.horeca.enums.PostStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.PostAttachmentRepository;
import uz.uzkassa.horeca.repository.PostRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PostService extends BaseService {

    PostRepository postRepository;
    PostAttachmentRepository postAttachmentRepository;

    @Transactional
    public Long create(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setPostDate(postDTO.getPostDate());
        post.setPostType(postDTO.getPostType());
        post.setBranchId(Optional.ofNullable(postDTO.getBranchId()).orElse(getCurrentBranchId()));
        postRepository.save(post);
        if (postDTO.getAttachments() != null) {
            int sorder = 0;
            for (PostAttachmentDTO postAttachmentDTO : postDTO.getAttachments()) {
                PostAttachment postAttachment = new PostAttachment();
                postAttachment.setPostId(post.getId());
                postAttachment.setSorder(sorder++);
                if (postAttachmentDTO.getText() != null) {
                    postAttachment.setAttachmentType(AttachmentType.TEXT);
                    postAttachment.setDescription(postAttachmentDTO.getText());
                } else if (postAttachmentDTO.getPhotoId() != null) {
                    postAttachment.setPhotoId(postAttachmentDTO.getPhotoId());
                    postAttachment.setAttachmentType(AttachmentType.PHOTO);
                } else if (postAttachmentDTO.getPhotoGallery() != null && !postAttachmentDTO.getPhotoGallery().getPhotos().isEmpty()) {
                    for (Long photoGalleryId : postAttachmentDTO.getPhotoGallery().getPhotos()) {
                        postAttachment.setAttachmentType(AttachmentType.PHOTO_GALLERY);
                        postAttachmentRepository.save(postAttachment);
                        PostPhotoGallery postPhotoGallery = new PostPhotoGallery();
                        postPhotoGallery.setPhotoId(photoGalleryId);
                        postPhotoGallery.setStructure(postAttachmentDTO.getPhotoGallery().getStructure());
                        postAttachment.addPhotoGallery(postPhotoGallery);
                    }
                } else if (postAttachmentDTO.getVideoUrl() != null) {
                    postAttachment.setVideoUrl(postAttachmentDTO.getVideoUrl());
                    postAttachment.setAttachmentType(AttachmentType.VIDEO);
                } else if (postAttachmentDTO.getAddress() != null) {
                    postAttachment.setAddress(addressMapper.toEntity(postAttachmentDTO.getAddress()));
                    postAttachment.setAttachmentType(AttachmentType.ADDRESS);
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
                post.setBranchId(postDTO.getBranchId());
                if (postDTO.getAttachments() != null) {
                    postAttachmentRepository.deleteAllByPostId(post.getId());
                    int sorder = 0;
                    for (PostAttachmentDTO postAttachmentDTO : postDTO.getAttachments()) {
                        PostAttachment postAttachment = new PostAttachment();
                        postAttachment.setPostId(post.getId());
                        postAttachment.setSorder(sorder++);
                        if (postAttachmentDTO.getText() != null) {
                            postAttachment.setAttachmentType(AttachmentType.TEXT);
                            postAttachment.setDescription(postAttachmentDTO.getText());
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
                        } else if (postAttachmentDTO.getVideoUrl() != null) {
                            postAttachment.setVideoUrl(postAttachmentDTO.getVideoUrl());
                            postAttachment.setAttachmentType(AttachmentType.VIDEO);
                        } else if (postAttachmentDTO.getAddress() != null) {
                            postAttachment.setAddress(addressMapper.toEntity(postAttachmentDTO.getAddress()));
                            postAttachment.setAttachmentType(AttachmentType.ADDRESS);
                        }
                        postAttachmentRepository.save(postAttachment);
                    }
                }
                postRepository.save(post);
                return post.getId();
            })
            .orElseThrow(notFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
    }

    public PostDetailDTO get(Long id) {
        return postRepository
            .findById(id)
            .map(post -> {
                PostDetailDTO postDetailDTO = new PostDetailDTO();
                postDetailDTO.setId(post.getId());
                postDetailDTO.setTitle(post.getTitle());
                postDetailDTO.setBranch(post.getBranch().toCommonDTO());

                List<PostAttachment> postAttachments = postAttachmentRepository.findAllByPostIdOrderBySorder(id);
                for (PostAttachment postAttachment : postAttachments) {
                    PostAttachmentDetailDTO attachmentDetailDTO = new PostAttachmentDetailDTO();
                    attachmentDetailDTO.setAttachmentType(postAttachment.getAttachmentType());
                    if (postAttachment.getDescription() != null) {
                        attachmentDetailDTO.setText(postAttachment.getDescription());
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
                        for (Integer structureId : photoGalleriesMap.keySet()) {
                            PostPhotoGalleryDetailDTO postPhotoGalleryDetailDTO = new PostPhotoGalleryDetailDTO();
                            postPhotoGalleryDetailDTO.setStructure(structureId);
                            postPhotoGalleryDetailDTO.setPhotos(photoGalleriesMap.get(structureId));
                            attachmentDetailDTO.setPhotoGallery(postPhotoGalleryDetailDTO);
                        }
                    } else if (postAttachment.getVideoUrl() != null) {
                        attachmentDetailDTO.setVideoUrl(postAttachment.getVideoUrl());
                    } else if (postAttachment.getAddress() != null) {
                        attachmentDetailDTO.setAddress(addressMapper.toDetailDTO(postAttachment.getAddress()));
                    }
                    postDetailDTO.addAttachment(attachmentDetailDTO);
                }

                return postDetailDTO;
            })
            .orElseThrow(notFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
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
                postListDTO.setBranch(post.getBranch().toCommonDTO());
                postListDTO.setPostDate(post.getPostDate());
                return postListDTO;
            })
            .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        return postRepository.getResultList(filter).getList().stream().map(Post::toCommonDTO).collect(Collectors.toList());
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
            .orElseThrow(notFoundExceptionThrow(Post.class.getSimpleName(), "id", id));
    }
}
