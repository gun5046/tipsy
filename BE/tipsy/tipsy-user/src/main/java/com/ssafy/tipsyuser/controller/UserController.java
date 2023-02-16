package com.ssafy.tipsyuser.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainrdb.vo.ReportVo;
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
	@ApiOperation(value = "로그인", notes = "카카오 로그인 후 서버에 정보가 있는지 확인 후 결과 전송")
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
	@ApiOperation(value = "회원 가입", notes = "kakao_id, name, nickname 은 NonNull")
	public boolean registUser(@RequestBody UserVo userVo) {
		System.out.println(userVo.getGender());
		int n = userServiceImpl.registUser(userVo);
		if (n != 0) {
			logger.info("sdq");
			return true;
		} else {
			return false;
		}
	}

	@GetMapping("/nickname")
	@ApiOperation(value = "중복 확인", notes = "사용자가 입력한 닉네임이 중복인지 확인")
	public boolean checkName(@RequestParam String nickname) {
		int n =userServiceImpl.checkNickname(nickname);
		if(n==0) {
			return true;
		}else {
			return false;
		}
	}
	
	@PostMapping("/check") // Mobile
	@ApiOperation(value = "모바일 앱 로그인", notes = "kako_id, birth, gender, image를 body로 보내면 loginDto를 보내줌")
	public LoginDto checkUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody KakaoAccountDto accountDto) {
		logger.info("sdqds");

		LoginDto loginDto = userServiceImpl.checkUser("mobile", accountDto);
		if (loginDto.getUserCheck()) {
		Cookie cookie1 = new Cookie("Authorization", loginDto.getTokenDto().getAccessToken());
		Cookie cookie2 = new Cookie("RefreshToken", loginDto.getTokenDto().getRefreshToken());
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
	@ApiOperation(value = "유저 정보 조회", notes = "uid를 보내면 서버에서 사용자 정보를 보내줌")
	public UserVo getUserInfo(@RequestParam Long uid) {
		return userServiceImpl.getUserInfo(uid);
	}
	
	@PutMapping("/mypage/modify")
	@ApiOperation(value = "유저 정보 수정", notes = "사용자 정보를 보내면 DB에 업데이트 한 결과 알려줌")
	public Boolean updateUserInfo(@RequestBody UserVo userVo) {
		if(userServiceImpl.updateUserInfo(userVo) == 0) return false;
		else {
			return true;
		}
	}
	
	@DeleteMapping("/delete")
	@ApiOperation(value = "회원탈퇴", notes = "uid를 통해 사용자정보를 삭제한다.")
	public boolean deleteUser(@RequestParam Long uid) {
		int n = userServiceImpl.deleteUser(uid);
		if (n != 0) {
			logger.info("delete success");
			return true;
		} else {
			return false;
		}
	}
	
	@PostMapping("/report")
	@ApiOperation(value = "회원신고", notes = "user1이 user2를 conent의 내용으로 신고한다.")
	public boolean reportUser(@RequestBody ReportVo reportvo) {
		int n = userServiceImpl.reportUser(reportvo);
		if (n != 0) {
			logger.info(reportvo.getFrom() + " reported " + reportvo.getTo() + " as " + reportvo.getContent());
			return true;
		} else {
			return false;
		}
	}
}
