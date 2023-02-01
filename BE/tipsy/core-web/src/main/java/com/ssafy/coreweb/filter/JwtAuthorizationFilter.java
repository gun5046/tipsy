package com.ssafy.coreweb.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.coreweb.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    	
    	// 헤더에서 JWT 를 받아옵니다.
        String token = jwtTokenProvider.resolveToken(req); //request.getHeader("Authorization");
        // 유효한 토큰인지 확인합니다.
        
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            //res.addHeader("Authorization", "Bearer "+ authentication.getName());
            chain.doFilter(req, res);
            System.out.println("인증안됨");
            return;
        }
        // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
        String uid = jwtTokenProvider.getUserPk(token);
        
        // 서명이 정상적이면
        if(uid !=null) {
        	
        }
    }
}