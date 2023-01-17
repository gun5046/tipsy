package com.hanjan.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FriendsDto {
	private long user1;
	private long user2;
}
