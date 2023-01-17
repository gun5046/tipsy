package com.hanjan.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FriendsRequestDto {
	private long rid;
	private long uid;
	private String time;
}
