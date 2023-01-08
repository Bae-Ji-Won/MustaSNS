package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Domain.dto.User.UserJoinDto;
import com.likelionfinalproject1.Domain.dto.User.UserJoinRequest;
import com.likelionfinalproject1.Domain.dto.User.UserLoginRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.UserRepository;
import com.likelionfinalproject1.Service.Exception.UserException;
import com.likelionfinalproject1.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    private final UserException userException;

    @Value("${jwt.token.secret}")
    private String secretkey;   // application.yml에서 설정한 token 키의 값을 저장함
    private long expireTimeMs = 1000*60*60; // 토큰 1시간

    // 회원가입 기능
    public UserJoinDto join(UserJoinRequest userJoinRequest){
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(userEntity -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME,String.format(userJoinRequest.getUserName())+"는 이미 있습니다.");
                });

        User userEntity = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinDto.fromEntity(userEntity);
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


    // 모든 유저 데이터 출력
    public Page<User> findAllByPage(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }

    // 해당 유저 데이터 출력
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        return user;
    }

    // 권한 변경
    @Transactional      // 영속성으로 update를 하고 있기 때문에 이벤트 발생시 db 트랜잭션 자동으로 commit 해줌
    public void userToAdmin(Long userid,String name) {
        User user = userRepository.findById(userid)         // userid에 해당하는 데이터 호출
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        
        User admincheck = userRepository.findByUserName(name)       // 현재 토큰에 담겨있는 username을 통한 데이터 호출
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        
        if(admincheck.getRole() != UserRole.ADMIN){     // 만약 현재 토큰의 유저의 권한이 관리자가 아니라면 권한 없음 에러 반환
            throw  new AppException(ErrorCode.INVALID_PERMISSION);
        }

         user.roleUpdate(UserRole.ADMIN);
    }
}
