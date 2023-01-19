package com.hanjan.user.service;

import org.springframework.stereotype.Component;

import com.hanjan.user.data.vo.UserVo;
import com.hanjan.user.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenService {
	private final JwtTokenProvider jwtTokenProvider;
	
	public String createJwtToken(UserVo userVo) {
		jwtTokenProvider.createToken(userVo);
		return null;
		
	}
}
