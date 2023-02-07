package com.ssafy.tipsyuser.service;

import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.dto.KakaoAccountDto;
import com.ssafy.tipsyuser.dto.LoginDto;

public interface UserService {
	String getAccessToken(String code);

	KakaoAccountDto getKakaoUserInfo(String access_token);

	LoginDto checkUser(String type, KakaoAccountDto accountDto);

	int registUser(UserVo userVo);
	
	UserVo getUserInfo(Long uid);
	
	int checkNickname(String nickname);

	int updateUserInfo(UserVo userVo);
}
