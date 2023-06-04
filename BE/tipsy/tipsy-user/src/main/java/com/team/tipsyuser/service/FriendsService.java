package com.team.tipsyuser.service;

import java.util.List;

import com.team.domainrdb.vo.UserVo;

public interface FriendsService {
	public List<UserVo> getFriendsList(Long uid);
	
	public int insertFriend(Long user1,Long user2);

	public int requestFriend(Long user1, Long user2);
	
	public int deleteFriend(Long user1, Long user2);
}
