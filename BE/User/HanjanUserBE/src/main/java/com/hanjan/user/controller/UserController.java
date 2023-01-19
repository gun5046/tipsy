package com.hanjan.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanjan.user.data.dto.KakaoAccountDto;
import com.hanjan.user.data.dto.LoginDto;
import com.hanjan.user.data.vo.UserVo;
import com.hanjan.user.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserServiceImpl userServiceImpl;

	@GetMapping("/login")
	public LoginDto loginUser(@RequestParam(required = false) String code,
			@RequestParam(required = false) String state, @RequestParam(required = false) String error,
			@RequestParam(required = false) String error_description) {
		if (code == null)
			logger.info("code null"); // error thow 할 것

		String access_token = userServiceImpl.getAccessToken(code);

		if (access_token != null) {
			logger.info("token_null");// error thow 할 것
		}
		KakaoAccountDto accountDto = userServiceImpl.getKakaoUserInfo(access_token);
		
		return userServiceImpl.checkUser("web", accountDto);
	}

	@PostMapping("/")
	public boolean registUser(@RequestBody UserVo userVo) {
		int n = userServiceImpl.registUser(userVo);
		if(n!=0) {
			logger.info("추가 안됨");
			return true;
		}else {
			return false;
		}
	}
	
	@GetMapping("/check")
	public LoginDto checkUser(@RequestBody KakaoAccountDto accountDto) {
		logger.info("체크유저");
		
		LoginDto loginDto = userServiceImpl.checkUser("mobile", accountDto);
	
		return null;
	}
}
