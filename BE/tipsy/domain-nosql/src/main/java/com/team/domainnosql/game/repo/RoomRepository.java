package com.team.domainnosql.game.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.team.domainnosql.entity.Member;
import com.team.domainnosql.entity.Room;

public interface RoomRepository extends CrudRepository<Room, String> {
	List<Room> findAllByCode(String rid);
}
