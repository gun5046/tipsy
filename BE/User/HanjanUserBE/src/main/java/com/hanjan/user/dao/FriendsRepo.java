package com.hanjan.user.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hanjan.user.data.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FriendsRepo {
	private final FriendsDao friendsDao;
		// TODO Auto-generated method stub
	public List<UserVo> getFriendsList(Long uid) {
		return friendsDao.findFriendsInfoByUid(uid);
	}
	
	public int insertFriend(Long user1, Long user2) {
		return friendsDao.insertFriend(user1, user2);
	}
}
