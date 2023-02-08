package com.ssafy.tipsyroom.service;

import java.util.List;
import java.util.Map;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;

public interface RoomService {
	String createRoom(Room room);
	int enterRoom(Member member);
	void exitRoom(User user);
	void banUser(User user);
	void changeSet(Room room);
	List<Map<Object, Object>> getTable(int bno);
	int[][] getBuilding();
}
