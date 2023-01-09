package com.likelionfinalproject1.Service.Exception;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserException {

    private final UserRepository userRepository;

    // 토큰의 유저에 대한 유저 데이터를 가져오고 DB에 User 데이터가 있는지 확인
    public User userDBCheck(String userName){
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    
    // Junit관련되는 코드에서 사용 - Junit에서 Service부분 Test에서는 예외처리가 Service안에 있어야 하고, Optional.empty()때문에 어쩔수 없이 사용
    public Optional<User> optionalUserDBCheck(String userName){
        return userRepository.findByUserName(userName);
    }
}
