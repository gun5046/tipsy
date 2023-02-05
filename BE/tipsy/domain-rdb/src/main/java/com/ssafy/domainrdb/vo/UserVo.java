package com.ssafy.domainrdb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
	private Long uid;
	private String kakao_id;
	private String email;
	private String name;
	private String nickname;
	private String image;
	private String birth;
	private String gender;
	private String interest;
	private String reportcnt;
	
	
}
