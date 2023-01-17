package com.hanjan.user.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

	@Override
	@Value("${KAKAO-KEY}")
	public HashMap<String, String> getAccessToken(String code) {
		// TODO Auto-generated method stub
		String access_Token = "";
	    String refresh_Token = "";
	    
	    
	    String reqURL = "https://kauth.kakao.com/oauth/token";

	    try {
	    	
	    	
	    	
	   
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    return null;

	}
	
}
