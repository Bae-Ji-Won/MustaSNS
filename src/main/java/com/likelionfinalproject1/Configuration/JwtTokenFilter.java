package com.likelionfinalproject1.Configuration;

import com.likelionfinalproject1.Domain.UserEntity;
import com.likelionfinalproject1.Service.UserService;
import com.likelionfinalproject1.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


// 토큰을 받아 인가를 판별하는 클래스
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    // OncePerRequestFilter : 매번 들어갈때마다 토큰을 체크를 하는 클래스

    private final UserService userService;
    private final String secretKey;     // 외부에서 받아오는 키와 개발자의 고유키가 일치하는지 확인하기 위해 secretKey가 필요함

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

// 외부에서 Header에 담겨져 있는 토큰을 받아 작업하는 곳
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);    // 외부에서 header에 인증 AUTHORIZATION 값(Bearer 토큰) 담아 전송하는 것을 받을 수 있다.
        log.info("authorizationHeader:{}",authorizationHeader);

        // 정상적인 토큰이 없는 경우(접근 차단)
        // 만약 Header에 토큰이 없거나 토큰이 Bearer로 시작하지 않다면 null을 반환하여 다음 체인(기능)으로 이동
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 정상적인 토큰이 있는 경우
        // header에 저장된 token분리
        String token;
        try {           
            token = authorizationHeader.split(" ")[1];        // 토큰값만 추출함(Header 토큰에는 "Bearer 토큰"이 실린다. 즉, split을 통해 띄어쓰기를 기준으로 1번재 배열의 값만 추출하면 토큰값이다)
            log.info("token:{}",token);
        }catch (Exception e){   // 토큰 구조가 이상한 경우
            log.error("token 추출에 실패 했습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token이 만료 되었는지 Check
        // 위에서 Header에서 토큰값만 추출한 값과 고유 비밀키를 넘겨줘 토큰 시간을 체크한다.
        if(JwtTokenUtil.isExpired(token, secretKey)){
            filterChain.doFilter(request, response);
            return;
        };

        // Token에서 Claim에서  UserName꺼내기
        String userName = JwtTokenUtil.getUserName(token,secretKey);    // 외부에서 받은 토큰에서 userName값을 추출함
        log.info("userName:{}",userName);

        // UserDetail 가져오기
        UserEntity user = userService.getUserByUserName(userName);  // 외부에서 받은 토큰에서 추출한 userName값을 통해 DB에서 해당 데이터를 찾는다.
        log.info("userRole :{}",user.getRole());

// 문 열어주는 곳(인가 허용)
        // principal에 이름을 넣어 controller에서 해당 이름을 호출하여 사용함
        // new SimpleGrantedAuthority(user.getRole().name()) = user.getRole().name()가 USER 권한을 가질때 해당 사람에게만 인가를 해주기 위해 명단 설정(만약 등급별로 나눌때는 여러개의 값이 들어갈 수 있으므로 List로 저장함)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name()))    );
        // 다음으로 권한을 넘겨주기 위해 details를 설정해준다.
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);  // 권한 부여(문 열어줌)
        // doFilter 다음 체인으로 넘어간다
        filterChain.doFilter(request, response);
    }
}
