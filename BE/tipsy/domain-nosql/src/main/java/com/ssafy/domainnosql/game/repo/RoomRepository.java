package com.ssafy.domainnosql.game.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.Room;

public interface RoomRepository extends CrudRepository<Room, String> {
	List<Room> findAllByCode(String rid);
}
