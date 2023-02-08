package com.ssafy.domainauth.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.domainauth.entity.Auth;


@Repository
public interface AuthRepository extends CrudRepository<Auth, Long>{
}
