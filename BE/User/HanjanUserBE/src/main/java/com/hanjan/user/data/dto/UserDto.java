package com.hanjan.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
	private long uid;
	private String email;
	private String name;
	private String nickname;
	private String image;
	private String birth;
	private boolean gender; // tinyint -> boolean  int
	private int reportcnt;
	
	
}
