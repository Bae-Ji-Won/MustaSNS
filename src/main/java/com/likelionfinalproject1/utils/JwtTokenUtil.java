package com.likelionfinalproject1.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

//    토큰 고유키값을 여기서 지정해서 할 수 있음. 그렇게 되면 아래 createToken에서 생성자에는 key값을 안받아도 됨
//    @Value("${jwt.token.secret}")
//    private String secretKey;

    // 외부에서 받아온 토큰을 고유 키값으로 데이터를 추출한다.
    private static Claims extractClaims(String token, String key) {
        // 현재 암호화 되어 있는 token을 고유 키값을 가지고 풀어낸다.
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();    
    }

    // 추출된 토큰에서 userName 찾기
    public static String getUserName(String token,String secretkey){
        return extractClaims(token, secretkey).get("userName").toString();  // 풀어낸 토큰값중 userName의 값을 반환해줌
   //     return extractClaims(token, secretkey).get("userName",String.class);  // 위에꺼랑 같은 의미임
    }

    // 추출된 토큰에서 토큰날짜 찾기
    public static boolean isExpired(String token, String secretkey) {
        // expire timestamp를 return함
        Date expiredDate = extractClaims(token, secretkey).getExpiration(); // 풀어낸 토큰값중 날짜만 가져옴
        return expiredDate.before(new Date());      // 받아온 토큰에 있는 날짜가 현재 시간보다 전인지 확인함
    }
    
    public static String createToken(String userName, long expireTimeMs, String key) {
        Claims claims = Jwts.claims(); // 일종의 map
        claims.put("userName", userName);

        return Jwts.builder()       // 토큰 생성
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))      //  시작 시간 : 현재 시간기준으로 만들어짐
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))     // 끝나는 시간 : 지금 시간 + 정해둔 시간
                .signWith(SignatureAlgorithm.HS256, key)        //  암호화 알고리즘, secret 값 세팅
                .compact();
    }
}
