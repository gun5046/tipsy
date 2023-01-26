package com.hanjan.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanjan.user.data.dto.FriendsDto;
import com.hanjan.user.data.vo.UserVo;
import com.hanjan.user.service.FriendsService;

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
	public boolean insertFriend(@RequestBody FriendsDto friends) { // insert -> user1 user2 둘다 추가
		int n = friendsService.insertFriend(friends.getUser1(), friends.getUser2());
		if(n==0) {
			return false;
		}else {
			return true;
		}
	}
	
	@DeleteMapping("/del")
	public boolean deleteFriend(@RequestBody FriendsDto friends) {
		int n = friendsService.deleteFriend(friends.getUser1(), friends.getUser2());
		if(n==0) {
			return false;
		}else {
			return true;
		}
	}
}
