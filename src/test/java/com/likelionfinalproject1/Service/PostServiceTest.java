package com.likelionfinalproject1.Service;


import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Fixture.PostEntityFixture;
import com.likelionfinalproject1.Repository.*;
import com.likelionfinalproject1.Service.Exception.AlarmException;
import com.likelionfinalproject1.Service.Exception.LikeException;
import com.likelionfinalproject1.Service.Exception.PostException;
import com.likelionfinalproject1.Service.Exception.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import static org.mockito.Mockito.*;




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

        when(userException.testGetUserByUserName(userName))
                .thenReturn(Optional.of(mockuser));          // userName을 통한 DB에서 user데이터 찾고 해당 Entity 반환
        when(postRepository.save(any()))
                .thenReturn(mockpost);          // post DB저장후 해당 데이터 Post Entity 반환

        PostCreateRequest request = new PostCreateRequest(title,body);

        Assertions.assertDoesNotThrow(() -> postService.postCreate(request,userName));
    }

    @Test
    @DisplayName("1. 포스트 등록 실패 - 로그인 안함")
    void postCreate_fail(){
        when(userException.getUserByUserName(userName))
                .thenReturn(null);           // userName을 통해 DB에서 해당 데이터를 찾았지만 없을 경우 상황 설정
        when(postRepository.save(any()))
                    .thenReturn(PostEntityFixture.postEntity("han","1234"));    // 포스트 데이터 저장

        PostCreateRequest request = new PostCreateRequest(title,body);

        AppException exception = Assertions.assertThrows(AppException.class,() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
     //   AppException exception = Assertions.assertThrows(AppException.class,() -> postService.postCreate(request,userName));
        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }
}