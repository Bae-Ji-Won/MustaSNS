package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.User.UserJoinDao;
import com.likelionfinalproject1.Domain.dto.User.UserJoinRequest;
import com.likelionfinalproject1.Domain.dto.User.UserLoginRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.UserRepository;
import com.likelionfinalproject1.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretkey;   // application.yml에서 설정한 token 키의 값을 저장함
    private long expireTimeMs = 1000*60*60; // 토큰 1시간

    // 회원가입 기능
    public UserJoinDao join(UserJoinRequest userJoinRequest){
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(userEntity -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME,String.format(userJoinRequest.getUserName())+"는 이미 있습니다.");
                });

        User userEntity = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinDao.fromEntity(userEntity);
    }

    // 로그인 기능
    public String login(UserLoginRequest userLoginRequest){
        String username = userLoginRequest.getUserName();
        String password = userLoginRequest.getPassword();

        log.info("사용자가 입력한 username : "+username);
        User user = userRepository.findByUserName(username)     // 유저가 입력한 데이터의 아이디를 가지고 DB에서 데이터를 찾음
                // 아이디가 존재하지 않을때
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND));


        // 아이디가 존재하지만 비밀번호가 틀릴때
        if(!encoder.matches(password,user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // 아이디가 존재하고 비밀번호도 일치할때
        return JwtTokenUtil.createToken(username,expireTimeMs,secretkey);
    }


    // UserName을 통해 DB에 데이터가 있는지 확인(유저가 존재하는지 확인)
    public User getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PERMISSION,String.format("해당 유저가 없습니다.")));
        return user;
    }


}
