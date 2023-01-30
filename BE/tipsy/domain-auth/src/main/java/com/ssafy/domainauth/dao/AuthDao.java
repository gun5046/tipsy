package com.ssafy.domainauth.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthDao {
	int findUserCountByUid(Long uid);
}
