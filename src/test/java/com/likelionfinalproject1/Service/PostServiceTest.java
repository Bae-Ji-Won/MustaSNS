package com.likelionfinalproject1.Service;


import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateRequest;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Fixture.PostEntityFixture;
import com.likelionfinalproject1.Fixture.UserEntityFixture;
import com.likelionfinalproject1.Repository.*;
import com.likelionfinalproject1.Service.Exception.AlarmException;
import com.likelionfinalproject1.Service.Exception.LikeException;
import com.likelionfinalproject1.Service.Exception.PostException;
import com.likelionfinalproject1.Service.Exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


import static org.mockito.Mockito.*;




@Slf4j
class PostServiceTest {
    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    LikeRepository likeRepository = mock(LikeRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);
    CommentRepository commentRepository = mock(CommentRepository.class);
    PostException postException = mock(PostException.class);
    UserException userException = mock(UserException.class);
    LikeException likeException = mock(LikeException.class);
    AlarmException alarmException = mock(AlarmException.class);

    // PostService에 Di를 통해 의존된 클래스를 TestCase에서는 생성자를 통해 의존시켜줌
    @BeforeEach
    void setUp(){
        postService = new PostService(postRepository, likeRepository,alarmRepository,commentRepository,postException,userException,likeException,alarmException);
    }


    Long postId = 1l;
    Long userId = 1l;
    String userName = "han";
    String password = "1234";
    String title = "title";
    String body = "body";
    String comment = "comment test!!";


    @Test
    @DisplayName("포스트 등록 성공")
    void postCreate_success(){
        
        Post mockpost = mock(Post.class);       // (Response) post entity 임의 mock 생성
        User mockuser = mock(User.class);       // (Response) user entity 임의 생성

        when(userException.optionalUserDBCheck(userName))
                .thenReturn(Optional.of(mockuser));          // userName을 통한 DB에서 user데이터 찾고 해당 Entity 반환
        when(postRepository.save(any()))
                .thenReturn(mockpost);          // post DB저장후 해당 데이터 Post Entity 반환

        PostCreateRequest request = new PostCreateRequest(title,body);

        Assertions.assertDoesNotThrow(() -> postService.postCreate(request,userName));
    }

    @Test
    @DisplayName("포스트 등록 실패 - 로그인 안함")
    void postCreate_fail(){

        when(userException.optionalUserDBCheck(userName))
                    .thenReturn(Optional.empty());           // userName을 통해 DB에서 해당 데이터를 찾았지만 없을 경우 상황 설정
        when(postRepository.save(any()))
                    .thenReturn(PostEntityFixture.postEntity("han","1234"));    // 포스트 데이터 저장

        PostCreateRequest request = new PostCreateRequest(title,body);

        AppException exception = Assertions.assertThrows(AppException.class,() -> postService.postCreate(request,userName));        // userName을 통해 찾은 데이터가 없으므로 예외처리가 발생함
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 조회 성공")
    void postGet_success(){
        User user = UserEntityFixture.userEntity(userName,password);
        Post post = PostEntityFixture.postEntity(userName,password);

        given(postException.postDBCheck(postId))
                .willReturn(post);

        PostOneResponse response = postService.getpostbyid(postId);
        assertEquals(userName,response.getUserName());
    }

    @Test
    @DisplayName("1. 수정 실패 - 포스트 존재하지 않음")
    void postUpdate_fail_one(){
        User user = UserEntityFixture.userEntity(userName,password);

        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.of(user));

        given(postException.optionalPostDBCheck(postId))
                .willReturn(Optional.empty());      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음

        PostCreateRequest request = new PostCreateRequest(title,body);

        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postupdate(postId,request,userName));  // postUpdate의 예외처리중 해당 Post가 없을때 예외처리 발생
        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("2. 수정 실패 - 작성자!=유저")
    void postUpdate_fail_two(){
        User user1 = UserEntityFixture.userEntity("user1","1234");
        User user2 = UserEntityFixture.userEntity("user2","1234");

        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.of(user2));                 // 현재 Post를 업데이트 할려고 하는 사람은 user2임

        given(postException.optionalPostDBCheck(postId))
                .willReturn(Optional.of(PostEntityFixture.postEntity(user1.getUserName(),user1.getPassword())));      // 현재 Post의 작성자는 user1임

        PostCreateRequest request = new PostCreateRequest(title,body);

        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postupdate(postId,request,userName));  // postUpdate의 예외처리중 Post작성자아 유저가 다를때 예외처리 발생 (rolecheck 메서드)
        assertEquals(ErrorCode.INVALID_PERMISSION, appException.getErrorCode());
    }


    @Test
    @DisplayName("3. 수정 실패 - 유저 존재하지 않음")
    void postUpdate_fail_three(){
        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.empty());      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음

        PostCreateRequest request = new PostCreateRequest(title,body);

        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postupdate(postId,request,userName));
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("1. 게시물 삭제 실패 - 유저 존재하지 않음")
    void postDelete_fail_one(){
        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.empty());      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음


        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postdelete(postId,userName));
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("2. 게시물 삭제 실패 - 포스트 존재하지 않음")
    void postDelete_fail_two(){
        User user = UserEntityFixture.userEntity(userName,password);

        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.of(user));

        given(postException.optionalPostDBCheck(postId))
                .willReturn(Optional.empty());      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음


        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postdelete(postId,userName));
        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("3. 게시물 삭제 실패 - 작성자!=유저")
    void postDelete_fail_three(){
        User user1 = UserEntityFixture.userEntity("user1","1234");
        User user2 = UserEntityFixture.userEntity("user2","1234");

        given(userException.optionalUserDBCheck(userName))
                .willReturn(Optional.of(user2));                 // 현재 Post를 업데이트 할려고 하는 사람은 user2임

        given(postException.optionalPostDBCheck(postId))
                .willReturn(Optional.of(PostEntityFixture.postEntity(user1.getUserName(),user1.getPassword())));      // 현재 Post의 작성자는 user1임

        AppException appException = Assertions.assertThrows(AppException.class, () -> postService.postdelete(postId,userName));
        assertEquals(ErrorCode.INVALID_PERMISSION, appException.getErrorCode());
    }
}