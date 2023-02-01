package com.ssafy.domainnosql.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVo {
	private String code;
	private String password;
	private long id;
	private String position;	
}
