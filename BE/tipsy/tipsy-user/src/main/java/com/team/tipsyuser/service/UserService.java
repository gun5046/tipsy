package com.team.tipsyuser.service;

import com.team.domainrdb.vo.ReportVo;
import com.team.domainrdb.vo.UserVo;
import com.team.tipsyuser.dto.KakaoAccountDto;
import com.team.tipsyuser.dto.LoginDto;

public interface UserService {
	String getAccessToken(String code);

	KakaoAccountDto getKakaoUserInfo(String access_token);

	LoginDto checkUser(String type, KakaoAccountDto accountDto);

	int registUser(UserVo userVo);
	
	UserVo getUserInfo(Long uid);
	
	int checkNickname(String nickname);

	int updateUserInfo(UserVo userVo);
	
	int deleteUser(Long uid);
	
	int reportUser(ReportVo reportvo);
}
