package com.hanjan.user.dao;

import org.springframework.stereotype.Component;

import com.hanjan.user.data.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepo {
	
	private final UserDao userDao;

	public UserVo checkUser(String kakao_id) {
		// TODO Auto-generated method stub
		UserVo user = userDao.findByKakaoID();
		
		return user;
	} 
}
