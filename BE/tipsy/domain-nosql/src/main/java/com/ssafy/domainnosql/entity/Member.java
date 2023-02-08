package com.ssafy.domainnosql.entity;

import lombok.Data;

@Data
public class Member {
	private String code;
	private String password;
	private Long id;
	private String position;	
}
