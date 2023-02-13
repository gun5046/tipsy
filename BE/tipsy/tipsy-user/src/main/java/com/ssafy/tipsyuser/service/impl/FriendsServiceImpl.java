package com.ssafy.tipsyuser.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.domainrdb.dao.user.FriendsDao;
import com.ssafy.domainrdb.vo.FriendRequestVo;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.service.FriendsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService{

	private final FriendsDao friendsDao;
	@Override
	public List<UserVo> getFriendsList(Long uid) {
		return friendsDao.findFriendsInfoByUid(uid);
	}

	@Override
	@Transactional
	public int insertFriend(Long user1, Long user2) {
		friendsDao.deleteFriendsRequest(user1, user2);
		
		return friendsDao.insertFriend(user1, user2);
	}

	@Override
	@Transactional
	public int requestFriend(Long user1, Long user2) {
		return friendsDao.insertFriendRequest(FriendRequestVo.builder().from(user1).to(user2).time(String.valueOf(LocalDateTime.now())).build());
	}

	@Override
	@Transactional
	public int deleteFriend(Long user1, Long user2) {
		return friendsDao.deleteFriend(user1, user2);
	}
	
	  

}
