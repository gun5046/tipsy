package com.hanjan.user.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hanjan.user.data.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepo {
	
	private final UserDao userDao;

	public UserVo checkUser(String id) {
		// TODO Auto-generated method stub
		UserVo user = userDao.findByKakaoID(id);
		return user;
	}
	
	@Transactional
	public int registUser(UserVo userVo) {
		return userDao.insertUser(userVo);
	}
}
