package com.ssafy.domainnosql.room.repo;

import java.util.List;
import java.util.Map;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;
import com.ssafy.domainnosql.entity.User;

public interface RoomRepo {
	void createRoom(Room room);
	void changeSet(Room room);

	int enterRoom(Member member);
	
	boolean exitRoom(User user);
	void banUser(User user);
	
	int[][] getBuilding();
	List<Map<Object, Object>> getTable(int bno);
	
	boolean isExists(String key);
	
}
