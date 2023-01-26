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

    // ������ ��ū ��ȿ�ð� 5��
    private final long accessTokenValidTime = 30 * 10 * 1000L;
    // �������� ��ū ��ȿ�ð� 1�ð�
    private final long refreshTokenValidTime = 1000 * 60 * 60;

    // ��ü �ʱ�ȭ, secretKey�� Base64�� ���ڵ��Ѵ�.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT ��ū ���� 
    public TokenDto createToken(UserVo userVo) {
        Claims claims = Jwts.claims().setSubject(Long.toString(userVo.getUid())); // JWT payload �� ����Ǵ� ��������, ���� ���⼭ user�� �ĺ��ϴ� ���� �ִ´�.
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
        System.out.println("token : " + accessToken+ "          " +refreshToken );
        return TokenDto.builder()
        		.accessToken(accessToken)
        		.refreshToken(refreshToken)
        		.accessTokenExpiresIn(new Date(now.getTime() + accessTokenValidTime))
        		.build();
    }

    // JWT ��ū���� ���� ���� ��ȸ
    public Authentication getAuthentication(String accessToken) {
    	
    	return new UsernamePasswordAuthenticationToken(getUserPk(accessToken), "");
    }

    // ��ū���� ȸ�� ���� ����
    public String getUserPk(String accessToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getSubject();
    }

    // Request�� Header���� token ���� �����ɴϴ�. "Authorization" : "TOKEN��'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // ��ū�� ��ȿ�� + �������� Ȯ��
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
}