package com.ssafy.domainnosql.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ssafy.domainnosql.entity.Member;

public interface RoomRepository extends CrudRepository<Member, String>{

	List<Member> findAllById(String rid);
	
}
