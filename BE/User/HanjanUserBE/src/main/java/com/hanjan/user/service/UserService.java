package com.hanjan.user.service;

import com.hanjan.user.data.dto.KakaoAccountDto;
import com.hanjan.user.data.dto.LoginDto;
import com.hanjan.user.data.vo.UserVo;

public interface UserService {

	String getAccessToken(String code);

	KakaoAccountDto getKakaoUserInfo(String access_token);

	LoginDto checkUser(String type, KakaoAccountDto accountDto);

	int registUser(UserVo userVo);
}
