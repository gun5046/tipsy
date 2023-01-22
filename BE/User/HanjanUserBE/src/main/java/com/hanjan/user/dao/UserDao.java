package com.hanjan.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.hanjan.user.data.vo.UserVo;

@Mapper
public interface UserDao {
	
	UserVo findByKakaoID(String id);
	int insertUser(UserVo userVo);
	UserVo findByUid();
}
