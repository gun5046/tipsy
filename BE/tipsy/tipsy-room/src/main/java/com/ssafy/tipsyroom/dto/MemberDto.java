package com.ssafy.tipsyroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MemberDto {
	private String code;
	private String password;
	private long id;
	private String position;	
}
