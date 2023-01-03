package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Domain.dto.Post.*;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.CommentRepository;
import com.likelionfinalproject1.Repository.PostRepository;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 권한 비교하는 코드 중복되어 따로 구분
    public void rolecheck(User user,String userName,Post post){
        if(user.getRole() != UserRole.ADMIN) {      // 현재 토큰의 유저의 권한이 ADMIN이 아닐때
            if (!userName.equals(post.getUserId().getUserName())) {     // 현재 토큰에 있는 아이디와 게시물의 아이디가 다를경우 예외처리
                throw new AppException(ErrorCode.INVALID_PERMISSION);
            }
        }
    }



    // 1. 포스트 새로 작성
    public PostCreateResponse postcreate(PostCreateRequest postCreateRequest, String userName){
        User user = userService.getUserByUserName(userName);    // 토큰에서 추출한 userName을 통해 User DB에서 해당 데이터 가져옴

        Post post = postRepository.save(postCreateRequest.toEntity(user));  // Post DB에 유저가 입력한 제목,내용 저장

        if(post == null){
            return PostCreateResponse.error();      // DB에서 오류가 발생하여 post값을 받아오지 못하면 "저장실패" 오류 출력
        }else {
            return PostCreateResponse.success(post.getId());    // 정상동작시 "등록 완료" 출력
        }
    }

    // 2. 포스트 한개만 호출하기
    public PostOneResponse getpostbyid(Long id){
        Post post = postRepository.findById(id)         // 현재 post 번호를 통해 데이터를 가져온다
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        return new PostOneResponse().fromEntity(post,post.getUserId().getUserName());

    }

    // 3. 포스트 리스트 모두 호출
    public Page<PostListResponse> findAllByPage(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);        // 모든 데이터 호출
        Page<PostListResponse> postListResponses = new PostListResponse().toDtoList(posts);     // Page<Entity> -> Page<Dto>변경
        return postListResponses;
    }

    // 4. 포스트 게시물 수정
    @Transactional      // jpa의 영속성을 활용할때는 Transactional을 통해 자동으로 DB에 추가가 될 수 있도록 해야한다.
    public PostChangeResponse postupdate(Long id, PostCreateRequest request, String userName) {
        User user = userRepository.findByUserName(userName)     // 토큰의 유저에 대한 유저 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        Post post = postRepository.findById(id)             // 게시물 번호에 대한 게시물 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));

        rolecheck(user,userName,post);          // 권한 확인(관리자나 게시물 작성자인지 아닌지 확인)

        if((!request.getTitle().equals(post.getTitle()))||(!request.getBody().equals(post.getBody()))){     // 서버에 저장되어 있는 데이터와 유저가 새로 입력한 데이터가 다를경우 덮어씌움
            post.update(request.getTitle(),request.getBody());      // Jpa 영속성을 활용한 update 기능 활용
        }

        // postRepository.save(post);

        String str = "포스트 수정 완료";
        return PostChangeResponse.success(str,post.getId());    // 정상동작시 "등록 완료" 출력
    }


    // 5. 게시물 삭제
    public PostChangeResponse postdelete(Long id, String userName) {
        User user = userRepository.findByUserName(userName)     // 토큰의 유저에 대한 유저 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        Post post = postRepository.findById(id)             // 게시물 번호에 대한 게시물 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));

        rolecheck(user,userName,post);

        postRepository.delete(post);

        String str = "포스트 삭제 완료";

        return PostChangeResponse.success(str,post.getId());
    }


}
