package com.team.tipsyuser.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.domainrdb.vo.FriendsVo;
import com.team.domainrdb.vo.UserVo;
import com.team.tipsyuser.service.FriendsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/friends")
@Api(value = "Friends")
public class FriendsController {
	Logger logger = LoggerFactory.getLogger(FriendsController.class);
	private final FriendsService friendsService;
	
	@GetMapping("/list")
	@ApiOperation(value = "친구목록 조회", notes = "uid의 친구목록을 가져온다")
	public List<UserVo> getFriendsList(Long uid) {
		return friendsService.getFriendsList(uid);
	}
	
	@PostMapping("/req")
	@ApiOperation(value = "친구 요청", notes = "user1이 user2에게 친구 요청을 한다.")
	public boolean requestFriend(@RequestBody FriendsVo friends) {
		int n = friendsService.requestFriend(friends.getUser1(), friends.getUser2());
		if(n == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@PostMapping("/duo")
	@ApiOperation(value = "친구 추가", notes = "user1과 user2 서로를 친구요청에서 지운 후 친구 목록에 추가한다.")
	public boolean insertFriends(@RequestBody FriendsVo friends) {
		int n = friendsService.insertFriend(friends.getUser1(), friends.getUser2());
		if(n==0) {
			return false;
		}else {
			return true;
		}
	}
	
	@PostMapping("/delete")
	@ApiOperation(value = "친구 삭제", notes = "user1과 user2 서로를 친구목록에서 삭제한다.")
	public boolean deleteFriend(@RequestBody FriendsVo friends) {
		int n = friendsService.deleteFriend(friends.getUser1(), friends.getUser2());
		if(n == 0) {
			return false;
		} else {
			return true;
		}
	}
}
