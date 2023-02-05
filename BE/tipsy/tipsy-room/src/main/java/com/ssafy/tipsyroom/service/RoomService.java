package com.ssafy.tipsyroom.service;

import com.ssafy.tipsyroom.dto.MemberDto;
import com.ssafy.tipsyroom.dto.RoomDto;

public interface RoomService {
	String createRoom(RoomDto roomdto);
	int enterRoom(MemberDto memberdto);
	int enterRoom2(MemberDto memberdto);
	void exitRoom(String roomcode, String uid);
	void banUser(String roomcode, String uid);
	void changeSet(RoomDto roomdto);
	int[][] getBuilding();
	void test();
}
