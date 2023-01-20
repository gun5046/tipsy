package com.hanjan.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanjan.user.data.dto.KakaoAccountDto;
import com.hanjan.user.data.dto.LoginDto;
import com.hanjan.user.data.vo.UserVo;
import com.hanjan.user.service.UserServiceImpl;

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
	@ApiOperation(value = "�� �α���", notes = "������ �α��� �� �� ���")
	public LoginDto loginUser(@RequestParam(required = false) String code,
			@RequestParam(required = false) String state, @RequestParam(required = false) String error,
			@RequestParam(required = false) String error_description) {
		if (code == null)
			logger.info("code null"); // error thow �� ��

		String access_token = userServiceImpl.getAccessToken(code);

		if (access_token != null) {
			logger.info("token_null");// error thow �� ��
		}
		KakaoAccountDto accountDto = userServiceImpl.getKakaoUserInfo(access_token);
		
		return userServiceImpl.checkUser("web", accountDto);
	}

	@PostMapping("/account")
	@ApiOperation(value = "�� ȸ�� ����", notes = "ȸ�� ���� �� �� ���, JWTToken �ο�")
	public boolean registUser(@RequestBody UserVo userVo) {
		int n = userServiceImpl.registUser(userVo);
		if(n!=0) {
			logger.info("�߰� �ȵ�");
			return true;
		}else {
			return false;
		}
	}
	
	@PostMapping("/check") // Mobile
	@ApiOperation(value = "����� �α���!", notes = "����� �α���, JwtToken �ο�")
	public LoginDto checkUser(@RequestBody KakaoAccountDto accountDto) {
		logger.info("üũ����");
		
		LoginDto loginDto = userServiceImpl.checkUser("mobile", accountDto);
	
		return loginDto;
	}
}
