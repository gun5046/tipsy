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
	
	String exitRoom(User user);
	void banUser(User user);
	
	List<int[]> getBuilding();
	List<Map<Object, Object>> getTable(int bno);
	
	void changeHost(User user);
	
	boolean isExists(String key);
	boolean checkMember(String uid, String rid);
	boolean checkRoom(String rid);
}
