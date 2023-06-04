package com.team.domainrdb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestVo {
	private Long rid;
	private Long from;
	private Long to;
	private String time;
	
}
