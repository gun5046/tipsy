package com.ssafy.domainauth.repo;

import org.springframework.data.repository.CrudRepository;

import com.ssafy.domainauth.entity.Auth;



public interface AuthRepository extends CrudRepository<Auth, Long>{

}
