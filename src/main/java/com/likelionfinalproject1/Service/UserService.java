package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.UserEntity;
import com.likelionfinalproject1.Domain.dto.UserJoinDao;
import com.likelionfinalproject1.Domain.dto.UserJoinRequest;
import com.likelionfinalproject1.Domain.dto.UserLoginRequest;
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
        userRepository.findByUserName(userJoinRequest.getUsername())
                .ifPresent(userEntity -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME,String.format(userJoinRequest.getUsername())+"는 이미 있습니다.");
                });

        UserEntity userEntity = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinDao.fromEntity(userEntity);
    }

    // 로그인 기능
    public String login(UserLoginRequest userLoginRequest){
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        log.info("사용자가 입력한 username : "+username);
        UserEntity user = userRepository.findByUserName(username)     // 유저가 입력한 데이터의 아이디를 가지고 DB에서 데이터를 찾음
                // 아이디가 존재하지 않을때
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOTFOUND,null));


        // 아이디가 존재하지만 비밀번호가 틀릴때
        if(!encoder.matches(password,user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,null);
        }

        // 아이디가 존재하고 비밀번호도 일치할때
        return JwtTokenUtil.createToken(username,expireTimeMs,secretkey);
    }
}
