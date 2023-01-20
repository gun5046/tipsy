package com.hanjan.user.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class FriendsDto {
	private Long user1;
	private Long user2;
}
