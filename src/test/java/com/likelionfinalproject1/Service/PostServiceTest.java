package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Repository.PostRepository;
import com.likelionfinalproject1.Repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class PostServiceTest {
    UserService userService;
    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

//    @BeforeEach
//    void setUp(){
//        postService = new PostService(postRepository, userService, userRepository);
//    }



//    @Test
//    @DisplayName("포스트 등록 성공")
//    void postCreate_success(){
//        Post postentity = mock(Post.class);
//        User userentity = mock(User.class);
//
//        given(userRepository.findByUserName(userentity.getUserName()))
//                .willReturn(Optional.of(userentity));       // userName을 통해 데이터 찾음
//        given(postRepository.save(any()))
//                .willReturn(postentity);        // postRepository에 저장하고 해당 데이터를 찾음
//
//    }
//
//    @Test
//    @DisplayName("포스트 등록 실패 - 유저 없음")
//    void postCreate_fail(){
//        Post postentity = mock(Post.class);
//        User userentity = mock(User.class);
//
//        given(userRepository.findByUserName(userentity.getUserName()))
//                .willReturn(Optional.empty());       // 해당하는 유저가 없음
//        given(postRepository.save(any()))
//                .willReturn(postentity);        // postRepository에 저장하고 해당 데이터를 찾음
//
//        AppException exception = Assertions.assertThrows
//        assertEquals();
//    }
}