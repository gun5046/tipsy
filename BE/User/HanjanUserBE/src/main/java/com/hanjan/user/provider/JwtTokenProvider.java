package com.hanjan.user.provider;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.hanjan.user.data.dto.TokenDto;
import com.hanjan.user.data.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
	@Value("${SECRET-KEY}")
    private String secretKey;

    // 엑세스 토큰 유효시간 5분
    private final long accessTokenValidTime = 30 * 10 * 1000L;
    // 리프레시 토큰 유효시간 1시간
    private final long refreshTokenValidTime = 1000 * 60 * 60;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성 
    public TokenDto createToken(UserVo userVo) {
        Claims claims = Jwts.claims().setSubject(Long.toString(userVo.getUid())); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        List<String>info = new ArrayList<>();
        info.add(userVo.getName());
        info.add(userVo.getNickname());
        
        Date now = new Date();
        
        String accessToken = Jwts.builder()
        		.setSubject(Long.toString(userVo.getUid()))
        		.claim("auth", info)
        		.setExpiration(new Date(now.getTime() + accessTokenValidTime))
        		.signWith(SignatureAlgorithm.HS256, secretKey)
        		.compact();
        
        String refreshToken = Jwts.builder()
        		.setExpiration(new Date(now.getTime() + refreshTokenValidTime))
        		.signWith(SignatureAlgorithm.HS256, secretKey)
        		.compact();
        
        return TokenDto.builder()
        		.accessToken(accessToken)
        		.refreshToken(refreshToken)
        		.accessTokenExpiresIn(new Date(now.getTime() + accessTokenValidTime))
        		.build();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String accessToken) {
    	
    	return new UsernamePasswordAuthenticationToken(getUserPk(accessToken), "");
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String accessToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
}