package com.team.domainauth.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.team.domainauth.entity.Auth;


@Repository
public interface AuthRepository extends CrudRepository<Auth, Long>{
}
