package com.ssafy.tipsyuser.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.dto.KakaoAccountDto;
import com.ssafy.tipsyuser.dto.LoginDto;
import com.ssafy.tipsyuser.service.impl.UserServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(value = "Login")
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserServiceImpl userServiceImpl;

	@GetMapping("/login")
	@ApiOperation(value = "웹 로그인", notes = "웹으로 로그인 할 때 사용")
	public LoginDto loginUser(@RequestParam(required = false) String code,
			@RequestParam(required = false) String state, @RequestParam(required = false) String error,
			@RequestParam(required = false) String error_description) {
		System.out.println(code);
		if (code == null)
			logger.info("code null"); // error thow 할 것

		String access_token = userServiceImpl.getAccessToken(code);

		if (access_token != null) {
			logger.info("token_null");// error thow 할 것
		}
		KakaoAccountDto accountDto = userServiceImpl.getKakaoUserInfo(access_token);
		
		return userServiceImpl.checkUser("web", accountDto);
	}

	@PostMapping("/account")
	@ApiOperation(value = "웹 회원 가입", notes = "회원 가입 할 때 사용, JWTToken 부여")
	public boolean registUser(@RequestBody UserVo userVo) {
		int n = userServiceImpl.registUser(userVo);
		if(n!=0) {
			logger.info("추가 안됨");
			return true;
		}else {
			return false;
		}
	}
	
	@PostMapping("/check") // Mobile
	@ApiOperation(value = "모바일 로그인!", notes = "모바일 로그인, JwtToken 부여")
	public LoginDto checkUser(@RequestBody KakaoAccountDto accountDto) {
		logger.info("체크유저");
		
		LoginDto loginDto = userServiceImpl.checkUser("mobile", accountDto);
	
		return loginDto;
	}
}
