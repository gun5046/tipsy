package com.hanjan.user.data.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVo {
	private long uid;
	private String kakao_id;
	private String name;
	private String nickname;
	private String image;
	private String birth;
	private boolean gender; // tinyint -> boolean  int
	private int reportcnt;
}
