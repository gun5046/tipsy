package com.ssafy.domainnosql.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash
public class Member {

	@Id
	private String code;
	private String password;
	private long uid;
	private String position;

}
