package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Alarm;
import com.likelionfinalproject1.Domain.Entity.Like;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Mypage.MypagelistResponse;
import com.likelionfinalproject1.Domain.dto.Post.*;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.*;
import com.likelionfinalproject1.Service.Exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final AlarmRepository alarmRepository;

    private final CommentRepository commentRepository;

    private final PostException postException;

    private final UserException userException;

    private final LikeException likeException;

    private final AlarmException alarmException;

    // --------------------- 예외 처리 ---------------------------

    // user와 post 존재여부 확인 예외처리를 하는 코드들이 많아서 코드중복되어 따로 분리해서 map으로 반환해줄려고 했는데
    // 어차피 map에서 꺼내서 각각 User와 Post에 넣어줘야 해서 이것또한 중복코드가 되기 때문에 아래 코드 사용 안함
    public Map<String,Object> userpostDBCheck(String userName, Long id){
        User user = userException.userDBCheck(userName);
        Post post = postException.postDBCheck(id);

        Map<String,Object> checklist = new HashMap<>();
        checklist.put("user",user);
        checklist.put("post",post);

        return checklist;
    }


    // --------------------- 게시물 기능 구현 ---------------------------

    // 1. 포스트 새로 작성
    public PostCreateResponse postCreate(PostCreateRequest postCreateRequest, String userName){
//        User user = userException.testGetUserByUserName(userName)
//                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PERMISSION,String.format("해당 유저가 없습니다.")));

        User user = userException.getUserByUserName(userName);    // 토큰에서 추출한 userName을 통해 User DB에서 해당 데이터 가져옴

        Post post = postRepository.save(postCreateRequest.toEntity(user));  // Post DB에 유저가 입력한 제목,내용 저장

        if(post == null){
            return PostCreateResponse.error();      // DB에서 오류가 발생하여 post값을 받아오지 못하면 "저장실패" 오류 출력
        }else {
            return PostCreateResponse.success(post.getId());    // 정상동작시 "등록 완료" 출력
        }
    }

    // 2. 포스트 한개만 호출하기
    public PostOneResponse getpostbyid(Long id){
        Post post = postException.postDBCheck(id);

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
        
        User user = userException.userDBCheck(userName);
        Post post = postException.postDBCheck(id);


        postException.rolecheck(user,userName,post);          // 권한 확인(관리자나 게시물 작성자인지 아닌지 확인)

        if((!request.getTitle().equals(post.getTitle()))||(!request.getBody().equals(post.getBody()))){     // 서버에 저장되어 있는 데이터와 유저가 새로 입력한 데이터가 다를경우 덮어씌움
            post.update(request.getTitle(),request.getBody());      // Jpa 영속성을 활용한 update 기능 활용
        }

        String str = "포스트 수정 완료";
        return PostChangeResponse.success(str,post.getId());    // 정상동작시 "등록 완료" 출력
    }


    // 5. 게시물 삭제
    public PostChangeResponse postdelete(Long id, String userName) {
        User user = userException.userDBCheck(userName);
        Post post = postException.postDBCheck(id);

        postException.rolecheck(user,userName,post);

        likeRepository.deleteAllByPost(post);   // 연결된 모든 like 데이터 삭제

        commentRepository.deleteAllByPost(post);    // 연결된 모든 댓글 데이터 삭제

        alarmRepository.deleteAllByTargetId(post.getId());         // 연결된 모든 alarm 데이터 삭제

        postRepository.delete(post);            // 게시물 데이터 삭제
        
        

        String str = "포스트 삭제 완료";



        return PostChangeResponse.success(str,post.getId());
    }

    // --------------------- 좋아요 기능 구현 ---------------------------

    // 1. 게시물 좋아요 누르기
    public String postlike(Long id, String userName) {

        User user = userException.userDBCheck(userName);
        Post post = postException.postDBCheck(id);


        int result = likeException.likecheck(user,post);     // 해당 게시물에 좋아요가 이미 있는지 없는지 체크함


        // 알림 설정(예외처리)
        Alarm alarm = alarmException.alarmDBcheck(user,post,"like");


        // result에 default값인 -1을 제외한 값이 들어있다면 이미 해당 게시물에 해당유저가 좋아요를 누른 상태이므로 해당 좋아요 데이터를 삭제(좋아요 취소)를 한다.
        if(result != -1){

            alarmException.alarmdelete(alarm,"new like!");         // 알림 삭제

            likeRepository.delete(post.getLikes().get(result));

            return "좋아요를 취소했습니다.";
        }

        // 알림 설정 예외처리 클래스에 작성하지 않은 이유는 이미 DB에 데이터가 있을경우 새로 생성하면 2개의 중복 데이터가 발생하기 때문
        alarmRepository.save(alarm);      // 포스터 주인앞으로 알림 데이터 저장함


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

        Post post = postException.postDBCheck(id);


        return post.getLikes().size();
    }

    // --------------------- MyPage 구현 ---------------------------

    // 1. 해당 유저가 작성한 게시물 모두 출력
    public Page<MypagelistResponse> mypage(String userName,Pageable pageable) {
        User user = userException.userDBCheck(userName);


        Page<Post> posts = postRepository.findAllByUserUserName(userName,pageable);   // 유저 이름을 통한 해당 post 찾기

        Page<MypagelistResponse> mypagelistResponses = new MypagelistResponse().toDtoList(posts);   // Entity -> MypagelistResponse

        return mypagelistResponses;
    }
}
