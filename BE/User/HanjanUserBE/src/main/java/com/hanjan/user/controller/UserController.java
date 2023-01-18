package com.hanjan.user.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanjan.user.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserServiceImpl userServiceImpl;
	
	@GetMapping("/login")
	public Map<String,Object> loginUser(@RequestParam(required = false) String code, 
			@RequestParam(required = false) String state, @RequestParam(required = false) String error, @RequestParam(required = false) String error_description) {
		if(code!=null) {
			String access_token= userServiceImpl.getAccessToken(code);
			
			if(access_token!=null) {
				Map<String, Object> userInfo = userServiceImpl.getKakaoUserInfo(access_token);
			}
		}
		return null;
	}
	
	@GetMapping("/check")
	public boolean checkUser(@RequestParam String id) {
		int n = userServiceImpl.checkUser(id);
		if(n==0) {
			return false;
		}else {
			return true;
		}
	}
}
