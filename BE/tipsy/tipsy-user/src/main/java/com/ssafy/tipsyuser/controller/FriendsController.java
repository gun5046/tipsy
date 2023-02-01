package com.ssafy.tipsyuser.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.domainrdb.vo.FriendsVo;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.service.FriendsService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendsController {
	
	private final FriendsService friendsService;
	
	@GetMapping("/list")
	public List<UserVo> getFriendsList(Long uid) {
		return friendsService.getFriendsList(uid);
	}
	
	@PostMapping("/duo")
	public boolean insertFriends(@RequestBody FriendsVo friends) {
		int n = friendsService.insertFriend(friends.getUser1(), friends.getUser2());
		if(n==0) {
			return false;
		}else {
			return true;
		}
	}
}
