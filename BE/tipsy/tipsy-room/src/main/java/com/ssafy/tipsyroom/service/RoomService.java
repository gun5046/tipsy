package com.ssafy.tipsyroom.service;

import java.util.List;
import java.util.Map;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;

public interface RoomService {
	String createRoom(Room room);
	int enterRoom(Member member);
	String exitRoom(User user);
	void banUser(User user);
	void changeSet(Room room);
	void changeHost(User user);
	List<Map<Object, Object>> getTable(int bno);
	List<int[]> getBuilding();
}
