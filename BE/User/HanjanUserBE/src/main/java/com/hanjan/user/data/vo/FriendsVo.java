package com.hanjan.user.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FriendsVo {
	private long user1;
	private long user2;
}
