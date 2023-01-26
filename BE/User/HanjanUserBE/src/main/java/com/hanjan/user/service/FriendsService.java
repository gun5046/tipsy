package com.hanjan.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hanjan.user.dao.FriendsRepo;
import com.hanjan.user.data.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsService {
	
	private final FriendsRepo friendsRepo;

	public List<UserVo> getFriendsList(Long uid) {
		// TODO Auto-generated method stub
		return friendsRepo.getFriendsList(uid);
	}
	
	public int insertFriend(Long user1,Long user2) {
		return friendsRepo.insertFriend(user1, user2);
	}

	public int deleteFriend(Long user1, Long user2) {
		// TODO Auto-generated method stub
		return friendsRepo.deleteFriend(user1,user2);
	}
	
}
