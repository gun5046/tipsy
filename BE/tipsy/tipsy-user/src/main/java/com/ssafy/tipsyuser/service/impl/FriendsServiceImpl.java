package com.ssafy.tipsyuser.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.domainrdb.dao.user.FriendsDao;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.service.FriendsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService{

	private final FriendsDao friendsDao;
	@Override
	public List<UserVo> getFriendsList(Long uid) {
		// TODO Auto-generated method stub
		return friendsDao.findFriendsInfoByUid(uid);
	}

	@Override
	@Transactional
	public int insertFriend(Long user1, Long user2) {
		// TODO Auto-generated method stub
		friendsDao.deleteFriendsRequest(user1, user2);
		
		return friendsDao.insertFriend(user1, user2);
	}

}
