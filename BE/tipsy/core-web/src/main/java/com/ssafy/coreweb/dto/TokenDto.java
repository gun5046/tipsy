package com.ssafy.coreweb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private Date accessTokenExpiresIn;
	private String Authority;
}
