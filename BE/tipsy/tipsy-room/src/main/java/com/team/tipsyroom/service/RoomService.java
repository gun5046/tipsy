package com.team.tipsyroom.service;

import java.util.List;
import java.util.Map;

import com.team.domainnosql.entity.Member;
import com.team.domainnosql.entity.Room;
import com.team.domainnosql.entity.User;
import com.team.domainrdb.vo.UserVo;

public interface RoomService {
	String createRoom(Room room);
	int enterRoom(Member member);
	String exitRoom(User user);
	void banUser(User user);
	void changeSet(Room room);
	void changeHost(User user);
	List<Map<Object, Object>> getTable(int bno);
	List<int[]> getBuilding();
	boolean findRoomByCode(String rid);
	UserVo getUserInfo(Long uid);
}
