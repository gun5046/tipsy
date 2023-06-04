package com.team.domainnosql.game.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.team.domainnosql.entity.Member;

public interface MemberRepository extends CrudRepository<Member, String>{
	List<Member> findMemberByCode(String rid);

	void findByCode(String rid);
	
}
