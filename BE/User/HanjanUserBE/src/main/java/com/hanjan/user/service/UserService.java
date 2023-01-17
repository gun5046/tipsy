package com.hanjan.user.service;

import java.util.HashMap;

public interface UserService {

	HashMap<String, String> getAccessToken(String code);
	
}
