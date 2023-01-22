package com.hanjan.user.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hanjan.user.data.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FriendsRepo {
	private final FriendsDao friendsDao;
		// TODO Auto-generated method stub
	Logger logger = LoggerFactory.getLogger(FriendsRepo.class);
	public List<UserVo> getFriendsList(Long uid) {
		return friendsDao.findFriendsInfoByUid(uid);
		
	}
	
	@Transactional
	public int insertFriend(Long user1, Long user2) {
		int n = friendsDao.deleteFriendRequest(user1,user2);
		if(n == 0 ) {
			logger.info("친구요청 없음. throw 필요");
		}
		return friendsDao.insertFriend(user1, user2);
	}

	@Transactional
	public int deleteFriend(Long user1, Long user2) {
		// TODO Auto-generated method stub
		int n = friendsDao.deleteFriend(user1, user2);
		if(n==0) {
			logger.info("erro throw");
		}
		return n;
	}
}
