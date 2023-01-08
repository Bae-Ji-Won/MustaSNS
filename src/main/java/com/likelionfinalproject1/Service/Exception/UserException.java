package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Component
public class UserException {

    private final UserRepository userRepository;

    // 토큰의 유저에 대한 유저 데이터를 가져오고 DB에 User 데이터가 있는지 확인
    public User userDBCheck(String userName){
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    // UserName을 통해 DB에 데이터가 있는지 확인(유저가 존재하는지 확인)
    public User getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PERMISSION,String.format("해당 유저가 없습니다.")));
        return user;
    }
}
