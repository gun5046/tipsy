package com.team.coreweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {
	private String key; // uid
	private String value; // token
	
	
	public RefreshTokenDto updateToken(String token) {
		this.value = token;
		return this;
	}
}
