package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Comment.CommentRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Fixture.CommentEntityFixture;
import com.likelionfinalproject1.Fixture.PostEntityFixture;
import com.likelionfinalproject1.Fixture.UserEntityFixture;
import com.likelionfinalproject1.Repository.AlarmRepository;
import com.likelionfinalproject1.Repository.CommentRepository;
import com.likelionfinalproject1.Service.Exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class CommentServiceTest {
    CommentService commentService;

    CommentRepository commentRepository = mock(CommentRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);
    PostException postException = mock(PostException.class);
    UserException userException = mock(UserException.class);
    
    CommentException commentException = mock(CommentException.class);
    AlarmException alarmException = mock(AlarmException.class);

    // PostService에 Di를 통해 의존된 클래스를 TestCase에서는 생성자를 통해 의존시켜줌
    @BeforeEach
    void setUp(){
        commentService = new CommentService(commentRepository,alarmRepository,postException,userException,commentException,alarmException);
    }

    Long postId = 1l;
    Long userId = 1l;

    Long commentId = 1l;
    String userName = "han";
    String password = "1234";
    String title = "title";
    String body = "body";
    String comment = "comment test!!";


    @Test
    @DisplayName("댓글 수정 실패(1) - 포스트 존재하지 않음")
    void commentUpdate_fail_one(){
        User user = UserEntityFixture.userEntity(userName,password);
        Post post = PostEntityFixture.postEntity(userName,password);
        Comment comment = CommentEntityFixture.commentEntity(userName,password);

        given(userException.userDBCheck(userName))
                .willReturn(user);     // 유저 데이터 존재

        given(postException.postDBCheck(postId))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));      // 포스트 데이터 존재

        given(commentException.commentDBCheck(commentId))
                .willReturn(comment);      // 댓글에 대한 데이터가 없을경우

        CommentRequest request = new CommentRequest("update comment");

        AppException appException = Assertions.assertThrows(AppException.class, () -> commentService.commentUpdate(postId,commentId,request,userName));  // commentUpdate의 예외처리중 해당 comment가 없을때 예외처리 발생
        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("2. 수정 실패 - 작성자!=유저")
    void commentUpdate_fail_two(){
        User user1 = UserEntityFixture.userEntity("user1","1234");
        User user2 = UserEntityFixture.userEntity("user2","1234");
        Post post = PostEntityFixture.postEntity(user2.getUserName(),user2.getPassword());
        Comment comment = CommentEntityFixture.commentEntity(user1.getUserName(),user1.getPassword());  // comment 주인은 user1

        given(userException.userDBCheck(userName))
                .willReturn(user2);                 // 현재 Comment를 업데이트 할려고 하는 사람은 user2임

        given(postException.postDBCheck(postId))
                .willReturn(post);

        given(commentException.commentDBCheck(commentId))
                .willReturn(comment);


        given(commentException.rolecheck(user2,comment))            // 댓글 작성자와 현재 user의 이름을 비교했으나 달라 예외처리함
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        CommentRequest request = new CommentRequest("update comment");

        AppException appException = Assertions.assertThrows(AppException.class, () -> commentService.commentUpdate(postId,commentId,request,userName));  // commentUpdate의 예외처리중 해당 comment가 없을때 예외처리 발생
        assertEquals(ErrorCode.INVALID_PERMISSION, appException.getErrorCode());
    }

    @Test
    @DisplayName("3. 수정 실패 - 유저 존재하지 않음")
    void commentUpdate_fail_three(){
        given(userException.userDBCheck(userName))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음

        CommentRequest request = new CommentRequest("update comment");

        AppException appException = Assertions.assertThrows(AppException.class, () -> commentService.commentUpdate(postId,commentId,request,userName));  // commentUpdate의 예외처리중 해당 comment가 없을때 예외처리 발생
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패(1) - 유저 존재하지 않음")
    void commentDelete_fail_one(){
        given(userException.userDBCheck(userName))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));      // postId를 통해 해당 Post Entity 데이터 찾지만 해당 데이터는 없음

        AppException appException = Assertions.assertThrows(AppException.class, () -> commentService.commentDelete(postId,commentId,userName));  // commentUpdate의 예외처리중 해당 comment가 없을때 예외처리 발생
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패(2) - 댓글이 존재하지 않음")
    void commentDelete_fail_two(){
        User user = UserEntityFixture.userEntity(userName,password);
        Post post = PostEntityFixture.postEntity(userName,password);
        Comment comment = CommentEntityFixture.commentEntity(userName,password);

        given(userException.userDBCheck(userName))
                .willReturn(user);     // 유저 데이터 존재

        given(postException.postDBCheck(postId))
                .willReturn(post);      // 포스트 데이터 존재

        given(commentException.commentDBCheck(commentId))
                .willThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND));      // 댓글에 대한 데이터가 없을경우


        AppException appException = Assertions.assertThrows(AppException.class, () -> commentService.commentDelete(postId,commentId,userName));  // commentUpdate의 예외처리중 해당 comment가 없을때 예외처리 발생
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, appException.getErrorCode());
    }
}