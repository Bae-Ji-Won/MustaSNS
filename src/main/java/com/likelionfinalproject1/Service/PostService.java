package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Like;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Domain.dto.Mypage.MypagelistResponse;
import com.likelionfinalproject1.Domain.dto.Post.*;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.CommentRepository;
import com.likelionfinalproject1.Repository.LikeRepository;
import com.likelionfinalproject1.Repository.PostRepository;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    private final LikeRepository likeRepository;


    // --------------------- 예외 처리 ---------------------------
    
    // 권한 비교하는 코드 중복되어 따로 구분
    public void rolecheck(User user,String userName,Post post){
        if(user.getRole() != UserRole.ADMIN) {      // 현재 토큰의 유저의 권한이 ADMIN이 아닐때
            if (!userName.equals(post.getUser().getUserName())) {     // 현재 토큰에 있는 아이디와 게시물의 아이디가 다를경우 예외처리
                throw new AppException(ErrorCode.INVALID_PERMISSION);
            }
        }
    }

    // 좋아요가 이미 있는지 없는지 체크
    public Integer likecheck(User user,Post post){
        // 양방향 매핑이 되어있는 Post, Like Entity에서 역방향으로 데이터를 구해서 비교하기
        int result = -1;
        // PostEntity에 객체형식으로 저장된 List<Like>가 얼마나 있는지 모르므로 사이즈만큼 값에서 0까지 반복해서 데이터를 찾는다
        for(int i=post.getLikes().size()-1; i>=0; i--){
            // PostEntity에 저장된 List<Like>중 현재 유저에게 입력받은 postId와 userId의 값을 가진 객체의 index값을 구함
            if((post.getLikes().get(i).getPost().getId() == post.getId())&&(post.getLikes().get(i).getUser().getId() == user.getId())){
                result = i;
            }
        }
        log.info("result :"+result);
        return result;
    }
    
    // --------------------- 게시물 기능 구현 ---------------------------

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

        return new PostOneResponse().fromEntity(post,post.getUser().getUserName());

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

        likeRepository.deleteAllByPost(post);

        postRepository.delete(post);

        String str = "포스트 삭제 완료";



        return PostChangeResponse.success(str,post.getId());
    }

    // --------------------- 좋아요 기능 구현 ---------------------------

    // 1. 게시물 좋아요 누르기
    public String postlike(Long id, String userName) {
        User user = userRepository.findByUserName(userName)     // 토큰의 유저에 대한 유저 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        Post post = postRepository.findById(id)             // 게시물 번호에 대한 게시물 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));

        int result = likecheck(user,post);     // 해당 게시물에 좋아요가 이미 있는지 없는지 체크함

        // result에 default값인 -1을 제외한 값이 들어있다면 이미 해당 게시물에 해당유저가 좋아요를 누른 상태이므로 해당 좋아요 데이터를 삭제(좋아요 취소)를 한다.
        if(result != -1){
            likeRepository.delete(post.getLikes().get(result));
            return "좋아요를 취소했습니다.";
        }


        // 위에서 result의 값이 -1 그대로일경우 해당 게시물에 대한 해당 유저의 좋아요가 없으므로 좋아요 데이터를 생성한다.
        Like like = likeRepository.findByPostIdAndUserId(post.getId(),user.getId())     // postid와 userid로 like 데이터를 찾음
                .orElse(likeRepository.save(Like.builder()
                                .post(post)
                                .user(user)
                                .build()));

        return "좋아요를 눌렀습니다.";

    }

    // 2. 해당 포스터 좋아요 총 개수 구하기
    public Integer postlikecount(Long id) {
        Post post = postRepository.findById(id)             // 게시물 번호에 대한 게시물 데이터를 가져옴
                .orElseThrow(() -> new AppException(ErrorCode.DATABASE_ERROR));

        return post.getLikes().size();
    }

    // --------------------- MyPage 구현 ---------------------------

    // 1. 해당 유저가 작성한 게시물 모두 출력
    public Page<MypagelistResponse> mypage(String userName,Pageable pageable) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));     // 해당 유저 존재여부 확인

        Page<Post> posts = postRepository.findAllByUserUserName(userName,pageable);   // 유저 이름을 통한 해당 post 찾기

        Page<MypagelistResponse> mypagelistResponses = new MypagelistResponse().toDtoList(posts);   // Entity -> MypagelistResponse

        return mypagelistResponses;
    }
}
