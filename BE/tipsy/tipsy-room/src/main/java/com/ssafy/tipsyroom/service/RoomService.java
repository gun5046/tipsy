package com.ssafy.tipsyroom.service;

import java.util.List;
import java.util.Map;

import com.ssafy.domainnosql.vo.MemberVo;
import com.ssafy.domainnosql.vo.RoomVo;

public interface RoomService {
	String createRoom(RoomVo roomvo);
	int enterRoom(MemberVo membervo);
	void exitRoom(String roomcode, String uid);
	void banUser(String roomcode, String uid);
	void changeSet(RoomVo roomvo);
	List<Map<Object, Object>> getTable(int bno);
	int[][] getBuilding();
}
