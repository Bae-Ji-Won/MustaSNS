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

        User user = userException.userDBCheck(userName);

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

        postException.rolecheck(user,post);          // 권한 확인(관리자나 게시물 작성자인지 아닌지 확인)

        if((!request.getTitle().equals(post.getTitle()))||(!request.getBody().equals(post.getBody()))){     // 서버에 저장되어 있는 데이터와 유저가 새로 입력한 데이터가 다를경우 덮어씌움
            post.update(request.getTitle(),request.getBody());      // Jpa 영속성을 활용한 update 기능 활용
        }

        String str = "포스트 수정 완료";
        return PostChangeResponse.success(str,post.getId());    // 정상동작시 "등록 완료" 출력
    }


    // 5. 게시물 삭제
    @Transactional
    public PostChangeResponse postdelete(Long id, String userName) {
        User user = userException.userDBCheck(userName);

        Post post = postException.postDBCheck(id);


        Boolean result = postException.rolecheck(user,post);          // 권한 확인(관리자나 게시물 작성자인지 아닌지 확인)

        if(result == false)     // postException.rolecheck의 값이 false이면 서로 다른 유저이므로 에외처리
            throw new AppException(ErrorCode.INVALID_PERMISSION);

        likeRepository.deleteAllByPost(post);   // 연결된 모든 like 데이터 삭제

        commentRepository.deleteAllByPost(post);    // 연결된 모든 댓글 데이터 삭제

        alarmRepository.deleteAllByTargetId(post.getId());         // 연결된 모든 alarm 데이터 삭제

        postRepository.delete(post);            // 게시물 데이터 삭제
        

        String str = "포스트 삭제 완료";

        return PostChangeResponse.success(str,post.getId());
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
