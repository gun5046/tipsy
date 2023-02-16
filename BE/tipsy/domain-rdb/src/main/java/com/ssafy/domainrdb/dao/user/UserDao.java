package com.ssafy.domainrdb.dao.user;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.domainrdb.vo.ReportVo;
import com.ssafy.domainrdb.vo.UserVo;

@Mapper
public interface UserDao {
	
	UserVo findByKakaoID(String id);
	int insertUser(UserVo userVo);
	UserVo findUserByUid(Long uid);
	UserVo findUserCountByNickname(String nickname);
	int updateUserInfo(UserVo userVo);
	int deleteUser(Long uid);
	int reportUser(ReportVo reportvo);
}