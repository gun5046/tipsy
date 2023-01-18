package com.hanjan.user.service;

import java.util.Map;

public interface UserService {

	String getAccessToken(String code);

	Map<String,Object> getKakaoUserInfo(String access_token);

	int checkUser(String code);

	
}
