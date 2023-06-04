package com.team.coreweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class TokenDto {
	private String accessToken;
	private String refreshToken;
}
