package com.ssafy.domainrdb.dao.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.domainrdb.vo.UserVo;

@Mapper
public interface FriendsDao {
	List<UserVo> findFriendsInfoByUid(Long uid);
	int insertFriend(Long user1, Long user2);
	int deleteFriendsRequest(Long user1, Long user2);
}
