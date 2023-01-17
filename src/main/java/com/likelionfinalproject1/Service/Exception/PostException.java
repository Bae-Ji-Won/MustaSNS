package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class PostException {

    private final PostRepository postRepository;

    // 게시물 번호에 대한 게시물 데이터를 가져오고 DB에 Post 데이터가 있는지 확인
    public Post postDBCheck(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }


    // 권한 비교하는 코드 중복되어 따로 구분
    // 만약 유저가 다를 경우 false반환, 같을경우 true반환
    // 여기서 예외처리 해도 되지만 Junit에서 인식을 못하므로 Service에서 진행
    public boolean rolecheck(User user,Post post){
        if(user.getRole() != UserRole.ADMIN) {      // 현재 토큰의 유저의 권한이 ADMIN이 아닐때
            if (!user.getUserName().equals(post.getUser().getUserName())) {     // 현재 토큰에 있는 아이디와 게시물의 아이디가 다를경우 예외처리
                throw new AppException(ErrorCode.INVALID_PERMISSION);
            }
        }
        return true;
    }


}