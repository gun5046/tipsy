package com.team.coreweb.provider;

import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.team.coreweb.dto.UserDto;
import com.team.domainauth.entity.Auth;
import com.team.domainauth.repo.AuthRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	@Value("${SECRET-KEY}")
    private String secretKey;

	private final AuthRepository authRepository;
    private final long accessTokenValidTime = 30 * 10 * 1000L;

    private final long refreshTokenValidTime = 1000 * 60 * 60*24*7;
    
    public String test = "test";

 
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

  
    public String createAccessToken(UserDto userDto) {
        Date now = new Date();
        
        String accessToken = Jwts.builder()
        		.claim("uid",userDto.getUid())
        		.claim("name", userDto.getName())
        		.claim("nickname", userDto.getNickname())
        		.setExpiration(new Date(now.getTime() + accessTokenValidTime))
        		.signWith(SignatureAlgorithm.HS256, secretKey)
        		.compact();

        
        return accessToken;
    }

    public String createRefreshToken() {
    	Date now = new Date();
    	
        String refreshToken = Jwts.builder()
        		.setExpiration(new Date(now.getTime() + refreshTokenValidTime))
        		.signWith(SignatureAlgorithm.HS256, secretKey)
        		.compact();
        
        return refreshToken;
    }

    public int getUserPk(String accessToken) {
        try {
        	return (int)Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().get("uid");
        }catch (ExpiredJwtException e) {
            return (int)e.getClaims().get("uid");
        }
    	
    }

    public String getUserName(String accessToken) {
    	try {
    	return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().get("name");
    	}catch(ExpiredJwtException e) {
    		return e.getClaims().get("name").toString();
    	}
    }
    
    public String getUserNickname(String accessToken) {
    	try {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().get("nickname");
    	}catch (ExpiredJwtException e) {
            return e.getClaims().get("nickname").toString();
        }
    }
 
    public void saveRefreshToken(Long uid, String refreshToken) {
//    	authRepository.save(new Auth(uid,refreshToken));
    }
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
        	return false;
        }
    }
    
}