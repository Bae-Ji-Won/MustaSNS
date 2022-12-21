package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.UserEntity;
import com.likelionfinalproject1.Domain.dto.UserJoinDao;
import com.likelionfinalproject1.Domain.dto.UserJoinRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    public UserJoinDao join(UserJoinRequest userJoinRequest){
        userRepository.findByUserName(userJoinRequest.getUsername())
                .ifPresent(userEntity -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME,String.format(userJoinRequest.getUsername())+"는 이미 있습니다.");
                });

        UserEntity userEntity = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserJoinDao.fromEntity(userEntity);
    }
}
