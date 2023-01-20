package com.hanjan.user.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
	private String key; // uid
	private String value; // token
	
	
	public RefreshToken updateToken(String token) {
		this.value = token;
		return this;
	}
}
