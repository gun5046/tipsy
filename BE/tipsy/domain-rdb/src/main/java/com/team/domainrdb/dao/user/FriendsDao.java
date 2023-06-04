package com.team.domainrdb.dao.user;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team.domainrdb.vo.FriendRequestVo;
import com.team.domainrdb.vo.UserVo;

@Mapper
public interface FriendsDao {
	List<UserVo> findFriendsInfoByUid(Long uid);
	int insertFriendRequest(FriendRequestVo friendrequestvo);
	int insertFriend(@Param("user1")Long user1, @Param("user2")Long user2);
	int deleteFriendsRequest(@Param("user1")Long user1, @Param("user2")Long user2);
	int deleteFriend(@Param("user1")Long user1, @Param("user2")Long user2);
}
