package com.team.domainnosql.entity;

import java.util.List;

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
public class Room {
	@Id
	private String code;
	private String title;
	private String password;
	private int max;
	private String time;
	private int entrance;
	private int silence;
	private List<String> hashtag;
}
