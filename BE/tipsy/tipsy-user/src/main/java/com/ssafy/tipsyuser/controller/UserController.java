package com.ssafy.tipsyuser.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.dto.KakaoAccountDto;
import com.ssafy.tipsyuser.dto.LoginDto;
import com.ssafy.tipsyuser.dto.UserInfoDto;
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
	private final JwtTokenProvider jwt;

	@GetMapping("/login")
	@ApiOperation(value = "asd", notes = "asd")
	public UserInfoDto loginUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String code, @RequestParam(required = false) String state,
			@RequestParam(required = false) String error, @RequestParam(required = false) String error_description) {

		if (code == null)
			logger.info("code null"); // error thow

		String access_token = userServiceImpl.getAccessToken(code);

		if (access_token == null) {
			logger.info("token_null");// error thow
		}
		KakaoAccountDto accountDto = userServiceImpl.getKakaoUserInfo(access_token);

		LoginDto loginDto = userServiceImpl.checkUser("web", accountDto);
		UserInfoDto userInfoDto = UserInfoDto.builder().userCheck(loginDto.getUserCheck()).userVo(loginDto.getUserVo())
				.build();
		if (loginDto.getUserCheck()) {
			Cookie cookie1 = new Cookie("Authorization", loginDto.getTokenDto().getAccessToken());
			Cookie cookie2 = new Cookie("RefreshToken", loginDto.getTokenDto().getRefreshToken());

			cookie1.setHttpOnly(true);
			cookie2.setHttpOnly(true);
			response.addCookie(cookie1); ///////// 나중에 따로 만들자
			response.addCookie(cookie2);
		}

		return userInfoDto;

	}

	@PostMapping("/account")
	@ApiOperation(value = "asdq", notes = "asdq")
	public boolean registUser(@RequestBody UserVo userVo) {
		int n = userServiceImpl.registUser(userVo);
		if (n != 0) {
			logger.info("sdq");
			return true;
		} else {
			return false;
		}
	}

	@PostMapping("/check") // Mobile
	@ApiOperation(value = "sdq!", notes = "sdqdq")
	public LoginDto checkUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody KakaoAccountDto accountDto) {
		logger.info("sdqds");

		LoginDto loginDto = userServiceImpl.checkUser("mobile", accountDto);
		Cookie cookie1 = new Cookie("Authorization", loginDto.getTokenDto().getAccessToken());
		Cookie cookie2 = new Cookie("RefreshToken", loginDto.getTokenDto().getRefreshToken());
		if (loginDto.getUserCheck()) {
			cookie1.setHttpOnly(true);
			cookie2.setHttpOnly(true);
			response.addCookie(cookie1); ///////// 나중에 따로 만들자
			response.addCookie(cookie2);
		}
		return loginDto;
	}

	@GetMapping("/token")
	public void checkToken() {
		return;
	}

	@GetMapping("/mypage")
	public UserVo getUserInfo(@RequestParam Long uid) {
		return userServiceImpl.getUserInfo(uid);
	}
}
