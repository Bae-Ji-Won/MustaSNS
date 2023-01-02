package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.UserRole;
import com.likelionfinalproject1.Domain.dto.User.*;
import com.likelionfinalproject1.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userRequest){
        UserJoinDao userJoinDao = userService.join(userRequest);
        return Response.success(new UserJoinResponse(userJoinDao.getId(),userJoinDao.getUsername()));
    }

    // 로그인
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest);
        return Response.success(new UserLoginResponse(token));
    }

    // 모든 유저 데이터 출력
    @GetMapping
    public Response<Page<User>> userFindAll(Pageable pageable){
        Page<User> userlist = userService.findAllByPage(pageable);
        return Response.success(userlist);
    }

    // 해당 유저 데이터 출력
    @GetMapping("/{id}")
    public Response<User> userFind(@PathVariable(name = "id") Long id){
        User user = userService.findById(id);
        return Response.success(user);
    }

    // 관리자 권한 부여 기능 userid를 보냄
    @PostMapping("/{id}/role/change")
    public Response<String> userToAdmin(@PathVariable(name = "id")Long userid, Authentication authentication){
        String name = authentication.getName();
        userService.userToAdmin(userid,name);
        return Response.success(String.format(name+"에 해당하는 유저의 권한이 변경됬습니다."));
    }
}
