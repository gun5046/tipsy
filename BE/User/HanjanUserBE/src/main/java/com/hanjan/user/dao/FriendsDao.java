package com.hanjan.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hanjan.user.data.vo.UserVo;

@Mapper
public interface FriendsDao {

	List<UserVo> findFriendsInfoByUid(Long uid);
	int insertFriend(Long user1, Long user2);
	int deleteFriendRequest(Long user1, Long user2);
	int deleteFriend(Long user1,Long user2);
}
