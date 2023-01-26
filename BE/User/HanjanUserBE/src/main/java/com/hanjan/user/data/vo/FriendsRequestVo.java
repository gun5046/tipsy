package com.hanjan.user.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FriendsRequestVo {
	private long rid;
	private long uid;
	private String time;
}
